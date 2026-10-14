import { provideHttpClient } from '@angular/common/http';
import {
  HttpTestingController,
  provideHttpClientTesting,
} from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { environment } from '../../../environments/environment';
import { MyRegistrationsResponse } from './my-registrations.models';
import { MyRegistrationsService } from './my-registrations-service';

describe('MyRegistrationsService', () => {
  let service: MyRegistrationsService;
  let httpTestingController: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    service = TestBed.inject(MyRegistrationsService);
    httpTestingController = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpTestingController.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('fetches the current user registrations', () => {
    const response: MyRegistrationsResponse = {
      customerUsername: 'tester',
      registrations: [
        {
          customerUsername: 'tester',
          registrationId: 'registration-1',
          orderId: 'order-1',
          bookingStatus: 'CONFIRMED',
          eventId: 1,
          eventName: 'Claypot Making Workshop',
          eventDescription: 'Hands-on experience and guidance',
          eventDate: '2027-09-10T10:00:00',
          availableSeats: 44,
          currency: 'INR',
          amount: 100,
        },
      ],
    };

    service.getMyRegistrations().subscribe((registrations) => {
      expect(registrations).toEqual(response);
    });

    const request = httpTestingController.expectOne(
      `${environment.apiUrl}/me/registrations`
    );
    expect(request.request.method).toBe('POST');
    expect(request.request.body).toEqual({});
    request.flush(response);
  });
});
