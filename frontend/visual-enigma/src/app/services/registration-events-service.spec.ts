import { TestBed } from '@angular/core/testing';

import { RegistrationEventsService } from './registration-events-service';

describe('RegistrationEventsService', () => {
  let service: RegistrationEventsService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(RegistrationEventsService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
