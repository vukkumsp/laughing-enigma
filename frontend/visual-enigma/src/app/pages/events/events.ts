import { HttpClient } from '@angular/common/http';
import { Component, inject, OnDestroy, OnInit, signal } from '@angular/core';
import { environment } from '../../../environments/environment';
import { EventCard } from './event-card';
import { RegistrationEventsService } from '../../services/registration-events-service/registration-events-service';
import { filter, take, takeWhile } from 'rxjs';
import { PaymentCheckout } from '../../services/payment-checkout/payment-checkout';

export interface Event {
  id: number;
  name: string;
  description: string;
  eventDate: string;
  availableSeats: number;
}

@Component({
  selector: 'app-events',
  imports: [EventCard],
  templateUrl: './events.html',
  styleUrl: './events.scss',
})
export class Events implements OnInit, OnDestroy {

  private readonly http = inject(HttpClient);
  private readonly paymentCheckoutService = inject(PaymentCheckout);
  
  private readonly apiUrl = environment.apiUrl;

  readonly events = signal<Event[]>([]);
  readonly loading = signal(true);
  readonly errorMessage = signal('');
  readonly registrationMessage = signal('');
  readonly registrationError = signal(false);
  readonly registeringEventId = signal<number | null>(null);
  readonly awaitingPaymentRequest = signal(false);

  private readonly registrationEventsService = inject(RegistrationEventsService);

  constructor() {
  }

  ngOnInit() {
    this.loadEvents();
  }

  ngOnDestroy() {
    this.registrationEventsService.disconnect();
  }

  private loadEvents(): void {
    this.http.get<Event[]>(`${this.apiUrl}/events`).subscribe({
      next: (events) => {
        this.events.set(events);
        this.loading.set(false);
      },
      error: () => {
        this.loading.set(false);
        this.errorMessage.set('We could not load the events. Please try again later.');
      }
    });
  }

  registerForEvent(eventId: number): void {

    if (this.registeringEventId() !== null) {
      return;
    }

    const registrationId = crypto.randomUUID();
    console.log('Generated registration ID:', registrationId);

    this.registrationMessage.set('');
    this.registrationError.set(false);
    this.registeringEventId.set(eventId);
    this.awaitingPaymentRequest.set(true);

    let paymentRequestReceived = false;

    //register for events for this registration
    this.registrationEventsService.events$
      .pipe(
        filter(event =>
          event.data.registrationId === registrationId &&
          (
            event.type === 'PAYMENT_REQUIRED' ||
            event.type === 'PAYMENT_SUCCESS' ||
            event.type === 'PAYMENT_FAILED'
          )
        ),
        takeWhile(event => event.type !== 'PAYMENT_SUCCESS' && event.type !== 'PAYMENT_FAILED', true)
      )
      .subscribe(event => {
        console.log('[events.ts] Opening Razorpay checkout with payment details:', event.data);
        if (event.type === 'PAYMENT_REQUIRED') {
          paymentRequestReceived = true;
          this.awaitingPaymentRequest.set(false);
          this.paymentCheckoutService.openCheckout(event.data, {
            onPaymentFailed: () => {
              this.awaitingPaymentRequest.set(false);
              this.registrationError.set(true);
              this.registrationMessage.set('Payment failed. Please try again.');
              this.registeringEventId.set(null);
              // Close the SSE connection after either payment success or failure
              this.registrationEventsService.disconnect();
            }
          });
        } else if (event.type === 'PAYMENT_FAILED') {
          this.awaitingPaymentRequest.set(false);
          this.registrationError.set(true);
          this.registrationMessage.set('Payment failed. Please try again.');
          this.registeringEventId.set(null);
          // Close the SSE connection after either payment success or failure
          this.registrationEventsService.disconnect();
        } else if (event.type === 'PAYMENT_SUCCESS') {
          this.events.update(events => events.map(event =>
            event.id === eventId
              ? { ...event, availableSeats: Math.max(0, event.availableSeats - 1) }
              : event
          ));

          this.awaitingPaymentRequest.set(false);
          this.registrationMessage.set('You registered for this event.');
          this.registeringEventId.set(null);
          this.registrationError.set(false);
          // Close the SSE connection after either payment success or failure
          this.registrationEventsService.disconnect();
        }
      });

    this.registrationEventsService.connectionEnded$
      .pipe(
        take(1)
      )
      .subscribe(() => {
        this.awaitingPaymentRequest.set(false);
        if (!paymentRequestReceived) {
          this.registeringEventId.set(null);
        }
      });

    // Open the SSE connection after subscribing so an early payment request is not missed.
    this.registrationEventsService.connect(registrationId);

    //send registration request to the backend
    this.http.post(`${this.apiUrl}/registrations`, { eventId, registrationId }).subscribe({
      next: () => {
        console.log('Registration started successfully');

        this.registrationMessage.set('You are registering for this event.');
        this.registrationError.set(false);
      },
      error: () => {
        this.awaitingPaymentRequest.set(false);
        this.registrationError.set(true);
        this.registrationMessage.set('Registration failed. Please try again.');
        this.registeringEventId.set(null);
        this.registrationEventsService.disconnect();
      }
    });


  }
}