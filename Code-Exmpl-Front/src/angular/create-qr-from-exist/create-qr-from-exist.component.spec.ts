import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CreateQrFromExistComponent } from './create-qr-from-exist.component';

describe('CreateFromExistComponent', () => {
  let component: CreateQrFromExistComponent;
  let fixture: ComponentFixture<CreateQrFromExistComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ CreateQrFromExistComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CreateQrFromExistComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
