# Stepper component description

 * Manual:
 
     * Include to your html component:   <app-stepper class="col-12"></app-stepper>
    
     * At all html-elements, which you want to be included in stepper, add class "step-tab"
 
     * In your component:
 
         1. ngOnInit - this.stepTabService.initTabs(); - init all the tabs. Use the class inheritor - StepTabService
 
         2. Add reference var to the service in the constructor  -  private stepTabService: StepTabService.
 
         3. And add StepTabService to components providers or to the parent component's providers
 
         4. Define subscription and init it in the constructor -  
         
                 this.finishSubscription = this.stepperService.notifyFinished().subscribe(isFinished => {
                                        if(isFinished){
                                        this.stepsFinished();
                                        }
                                    });                                      
                
         5. Define method stepsFinished() - to handle the result.         
          
         6. Add to tabs, which are must not be visible style="display: none"
