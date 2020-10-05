import { TestBed } from '@angular/core/testing';

import { StepTabService } from './step-tab.service';

describe('StepTabServiceService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: StepTabService = TestBed.get(StepTabService);
    expect(service).toBeTruthy();
  });
});
