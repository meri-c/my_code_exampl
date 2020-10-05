import {Component, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {Subscription} from "rxjs";
import {BigTabService} from "../../../service/stepper/big-tab.service";
import {HttpService} from "../../../service/utility/http.service";
import {FileQrCreationService} from "../../../service/component/file-qr-creation.service";
import {AlertComponent} from "../../utility/alert/alert.component";
import * as FileSaver from "file-saver";


/**
 * Create word doc file with qr-codes from the passed file with inventory nums.
 *
 * Work logic:
 *
 *    1. Select excel file in xml format with inventory nums inside
 *      (just one column, each cell has an inventory_num)
 *
 *    2. Press download
 *
 *    3. The app will show you the page with an inventory nums, which don't have any equipment yet
 *
 *    4. You can create ones on fly (i.e. You'll get a modal window with {@link EquipmentCreationComponent}
 *
 *    5. Or you can move forward and generate file from inventory nums which are already has qr-codes
 *
 *    6. The file (word doc) will be generated at servers' and returned to the page with the download btn.
 *
 * */
@Component({
  selector: 'app-file-qr-creation',
  templateUrl: './file-qr-creation.component.html',
  styleUrls: ['./file-qr-creation.component.css'],
  providers: [BigTabService, FileQrCreationService]
})
export class FileQrCreationComponent implements OnInit, OnDestroy {

  private equipment_created$: Subscription = new Subscription();
  private checkQrByInventory$: Subscription = new Subscription();

  allRequestedInventories: any [] = [];
  inventory_has_qr_obj: object;
  selectInventoryNums: Map<string, string> = new Map<string, string>();

  fileData: File;

  result_title = "Result";

  file_location: string;
  fileName: string = "bla.docx";

  @ViewChild(AlertComponent)
  alert: AlertComponent;

  constructor(private bigTabService: BigTabService, private http: HttpService, private sender: FileQrCreationService) {
  }

  ngOnInit() {
    this.bigTabService.initTabs("file-qr-creation");

    //waiting for subscription to notify, then remove created one from selectInventoryNums
    this.equipment_created$ = this.sender.getEquipmentCreated().subscribe(inv_num => {
      console.log("Created ", inv_num);
      this.removeFromSelectMap(inv_num);
    });

  }


  //------------------File handling---------------------//


  onFile(event: any) {
    this.fileData = event;
  }

  /**
   * Send file with inventory nums to server and get an object like {"inventory_num" : boolean_res, ...} in return
   * */
  onSubmitFile() {
    console.log("Submit");

    //send to server get map as a result
    this.checkQrByInventory$ = this.http.sendFileCheckInventoryHasQr(HttpService.urlPatternFileUpload + "check_inventory", this.fileData)
      .subscribe(data_map => {
        this.inventory_has_qr_obj = data_map;

        //at new tab show how many inventory_nums are without equipment in selector
        this.handleInventoryCheckResult(this.inventory_has_qr_obj);
      });
  }

  /**
   * Check if result inventory_nums array has false value.
   *
   * Push them to the array and show in the select. Change tab to a result one.
   * */
  handleInventoryCheckResult(result_obj: object) {
    console.log("a", this.selectInventoryNums.size);

    //check which inventory with missing qr
    for (var key in result_obj) {

      //add all of the inventory_nums to the array, store till the end, for the final doc

      this.allRequestedInventories.push(key);

      if (!result_obj[key]) {
        this.selectInventoryNums.set(key, key);
      }
    }

    console.log("b", this.selectInventoryNums.size);

    this.changeTabSetTitle(false);
  }

  //--------------------------//

  removeFromSelectMap(inventory_num: string) {
    if (this.selectInventoryNums !== undefined && inventory_num !== undefined) {
      this.selectInventoryNums.delete(inventory_num);
    }
  }

  getSelectedInventoryNum(event) {
    console.log("Got selected inv_num ", event);
    this.sender.sendPreSetInventoryNum(event);
  }


  //--------------------------//
  /**
   * Send the request with file location as a parameter to get doc with qr-codes
   * */
  downloadQrDoc() {
    console.log("Downloading doc...");
    //reply for a doc
    if(this.file_location !== undefined) {
      this.http.getQrFile(this.file_location).subscribe(response => {
        console.log("Ok");
        FileSaver.saveAs(response, this.fileName);
      });
    }else{
      console.log("File location is undefined. Can't load file");
    }
    //process
    //give back to user
  }

  /**
   * Valid inventory nums(which has qr-codes) sends to server. If document created, returns file location.
   *
   * Transform file location to file name and change the tab to the final one
   * */
  finishAndFormDoc() {
    //compare all inv and not found inventories
    let valid_inventory_nums = this.leaveValidInventoryNums(this.allRequestedInventories, Array.from(this.selectInventoryNums.keys()));
    //send result to server
    this.http.sendInventoriesGetQrDoc(valid_inventory_nums).subscribe(response => {

       //get location
        const location = response.headers.get("Location");
        console.log("File created ", location);
        this.file_location = location;

        //get fileName from location
        this.fileName = (function (location: string): string {
          let parsedFileLocation: string[] = location.split("\\");
          console.log(parsedFileLocation);

          let nameIndex = parsedFileLocation.length - 1;
          return parsedFileLocation[nameIndex];
        })(location);


        this.changeTabSetTitle(true);
      },
      error => {
        console.log("Error occurred during the file creation", error);
        this.alert.errorAlert("Помилка під час створенню файлу, спробуйте ще раз");
      });
  }

  /**
   * Separate only valid inventory nums from the rest ones. Valid - has equipment and qr-code in db.
   * */
  leaveValidInventoryNums(fullInventoryList: string[], notFoundList: string[]): string[] {

    console.log("All ", fullInventoryList);
    console.log("NotFound ", notFoundList);

    var temp_inv;

    for (var i = 0; i < notFoundList.length; i++) {
      temp_inv = fullInventoryList.indexOf(notFoundList[i]);
      if (temp_inv !== -1) {
        fullInventoryList.splice(temp_inv, 1);
      }
    }
    console.log("Result ", fullInventoryList);
    return fullInventoryList;
  }


  // --------------------------//

  changeTabSetTitle(isFinished: boolean) {
    //if all right - show final result page

    if (isFinished) {
      this.result_title = "Документ з QR-кодами сформовано. Натисніть кнопку для завантаження файлу";
    } else {
      this.result_title = "Недостатньо обладнання у базі для створення QR-коду";
    }

    //change tab
    this.changeBigTab();
  }

  changeBigTab() {
    console.log("change big tab");
    this.bigTabService.forward();
  }

  back() {
    this.bigTabService.back();
  }

//----------------------------

  closeAllAsyncSubscriptions() {
    this.checkQrByInventory$.unsubscribe();
    this.equipment_created$.unsubscribe();
  }

  ngOnDestroy(): void {
    this.closeAllAsyncSubscriptions();
  }

}
