import { inject, Service } from '@angular/core';
import { Auth } from './auth';
import { environment } from '../../environments/environment';
import { HttpClient } from '@angular/common/http';

@Service()
export class Payment {

    private readonly apiUrl = environment.apiUrl;
    private readonly auth = inject(Auth);
    private readonly http = inject(HttpClient);

    verifyPayment(registrationId: string, eventId: string, razorpayOrderId: string, razorpayPaymentId: string, razorpaySignature: string): void {
      this.http.post(`${this.apiUrl}/registrations/payment/verify`, 
            { registrationId, eventId, razorpayOrderId, razorpayPaymentId, razorpaySignature }).subscribe({
        next: (response: any) => {
          console.log('Verification started successfully:', response);
        },
        error: () => {
          console.log('Verification failed.');
        }
      });
    }
}
