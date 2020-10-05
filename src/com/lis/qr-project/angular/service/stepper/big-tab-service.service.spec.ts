import { TestBed } from '@angular/core/testing';

import { BigTabService } from './big-tab.service';

describe('BigTabServiceService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: BigTabService = TestBed.get(BigTabService);
    expect(service).toBeTruthy();
  });
});
