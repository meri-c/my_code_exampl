import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { FileQrCreationComponent } from './file-qr-creation.component';

describe('FileQrCreationComponent', () => {
  let component: FileQrCreationComponent;
  let fixture: ComponentFixture<FileQrCreationComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ FileQrCreationComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(FileQrCreationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
