import { HttpClient } from '@angular/common/http';
import { CurrencyPipe, DatePipe } from '@angular/common';
import { Component, computed, DestroyRef, inject, OnDestroy, OnInit, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { Router, RouterLink } from '@angular/router';
import { catchError, EMPTY, filter, take } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Event as EventContent } from '../../models/event.models';
import { RegistrationEventsService } from '../../services/registration-events-service/registration-events-service';
import { PaymentCheckout } from '../../services/payment-checkout/payment-checkout';

interface CheckoutPayment {
  registrationId: string;
  eventId: string;
  customerId: number | null;
  username: string;
  email: string;
  firstName: string;
  lastName: string;
  eventName: string;
  eventDate: string;
  amount: number;
  currency: string;
  orderId: string;
}

interface EventCheckout {
  eventId: number;
  eventName: string;
  eventDate: string;
}

@Component({
  selector: 'app-checkout',
  imports: [CurrencyPipe, DatePipe, RouterLink],
  templateUrl: './checkout.html',
  styleUrl: './checkout.scss',
})
export class Checkout implements OnInit, OnDestroy {
  private readonly http = inject(HttpClient);
  private readonly router = inject(Router);
  private readonly destroyRef = inject(DestroyRef);
  private readonly apiUrl = environment.apiUrl;
  private readonly registrationEvents = inject(RegistrationEventsService);
  private readonly paymentCheckout = inject(PaymentCheckout);

  readonly payment = signal(this.readCheckoutPayment());
  readonly eventCheckout = signal(this.readEventCheckout());
  readonly fromPendingPayments = signal(this.navigationState()?.['checkoutOrigin'] === 'payment-pending');
  readonly eventContent = signal<EventContent | null>(null);
  readonly loadingEvent = signal(false);
  readonly eventLoadError = signal('');
  readonly paymentStarted = signal(false);
  readonly registrationInProgress = signal(false);
  readonly registrationComplete = signal(false);
  readonly errorMessage = signal('');
  readonly couponCode = signal('');
  readonly appliedCoupon = signal('');
  readonly couponMessage = signal('');

  // Event
  readonly subtotal = computed(() => this.payment()?.amount ?? this.eventContent()?.price ?? null);
  readonly orderCurrency = computed(() => this.payment()?.currency ?? this.eventContent()?.currency ?? 'INR');
  readonly eventName = computed(() =>
    this.payment()?.eventName ?? this.eventCheckout()?.eventName ?? this.eventContent()?.name ?? '',
  );
  readonly eventDate = computed(() =>
    this.payment()?.eventDate ?? this.eventCheckout()?.eventDate ?? this.eventContent()?.eventDate ?? '',
  );

  // Coupon
  readonly couponDiscount = computed(() => {
    const subtotal = this.subtotal();
    return this.appliedCoupon() && subtotal !== null ? subtotal * 0.1 : 0;
  });
  readonly totalDue = computed(() => Math.max(0, (this.subtotal() ?? 0) - this.couponDiscount()));
 
  readonly attendeeName = computed(() => {
    const payment = this.payment();
    return payment ? `${payment.firstName} ${payment.lastName}`.trim() : 'Signed-in attendee';
  });
  readonly accountName = computed(() => this.payment()?.username ?? 'Your account');
  readonly backRoute = computed(() => {
    if (this.eventCheckout()) {
      return '/events';
    }

    return '/home';
  });
  readonly backLabel = computed(() =>
    this.fromPendingPayments() ? 'Back to pending payments' : 'Back to your events',
  );

  updateCouponCode(event: Event): void {
    if (event.target instanceof HTMLInputElement) {
      this.couponCode.set(event.target.value);
    }

    this.couponMessage.set('');
  }

  applyCoupon(): void {
    const code = this.couponCode().trim();
    if (!code) {
      this.couponMessage.set('Enter a coupon code to apply it.');
      return;
    }

    this.appliedCoupon.set(code.toUpperCase());
    this.couponMessage.set('10% discount preview applied. The actual payment will not change until coupon service integration.');
  }

  removeCoupon(): void {
    this.appliedCoupon.set('');
    this.couponCode.set('');
    this.couponMessage.set('Coupon removed.');
  }

  ngOnInit(): void {
    const eventCheckout = this.eventCheckout();
    if (!eventCheckout || this.payment()) {
      // Load Event Details only if we are in the event checkout flow and not in the payment flow
      // For payment flow, the event details are already available in the payment object via payment pending events
      return;
    }

    this.loadingEvent.set(true);
    this.http.get<EventContent>(
      `${this.apiUrl}/events/${encodeURIComponent(String(eventCheckout.eventId))}`,
    ).pipe(
      catchError(() => {
        this.eventLoadError.set('We could not load this event. Please go back and try again.');
        this.loadingEvent.set(false);
        return EMPTY;
      }),
      takeUntilDestroyed(this.destroyRef),
    ).subscribe(event => {
      this.eventContent.set(event);
      this.loadingEvent.set(false);
    });
  }

  ngOnDestroy(): void {
    this.registrationEvents.disconnect();
  }

  startPayment(): void {
    if (this.paymentStarted() || this.registrationComplete()) {
      return;
    }

    const payment = this.payment();
    if (payment) {
      this.openPaymentWindow(payment);
      return;
    }

    const eventCheckout = this.eventCheckout();
    if (!eventCheckout || !this.eventContent() || this.loadingEvent()) {
      return;
    }

    this.beginRegistration(eventCheckout.eventId);
  }

  private beginRegistration(eventId: number): void {
    const registrationId = crypto.randomUUID();
    let paymentRequestReceived = false;

    this.errorMessage.set('');
    this.registrationInProgress.set(true);
    this.registrationEvents.disconnect();

    this.registrationEvents.events$.pipe(
      filter(event =>
        event.data?.registrationId === registrationId
        && ['PAYMENT_REQUIRED', 'PAYMENT_SUCCESS', 'PAYMENT_FAILED'].includes(event.type),
      ),
      takeUntilDestroyed(this.destroyRef),
    ).subscribe(event => {
      if (event.type === 'PAYMENT_REQUIRED') {
        if (!this.isCheckoutPayment(event.data)) {
          this.failRegistration('We could not prepare your payment. Please try again.');
          this.registrationEvents.disconnect();
          return;
        }

        paymentRequestReceived = true;
        this.payment.set(event.data);
        this.registrationInProgress.set(false);
        this.openPaymentWindow(event.data);
      } else if (event.type === 'PAYMENT_SUCCESS') {
        paymentRequestReceived = true;
        this.paymentStarted.set(false);
        this.registrationInProgress.set(false);
        this.registrationComplete.set(true);
        this.errorMessage.set('');
        this.registrationEvents.disconnect();
      } else if (event.type === 'PAYMENT_FAILED') {
        paymentRequestReceived = true;
        this.failRegistration('Payment failed. You can try again.');
        this.registrationEvents.disconnect();
      }
    });

    this.registrationEvents.connectionEnded$.pipe(
      take(1),
      takeUntilDestroyed(this.destroyRef),
    ).subscribe(() => {
      if (!paymentRequestReceived && this.registrationInProgress()) {
        this.failRegistration('We could not start checkout. Please try again.');
      }
    });

    this.registrationEvents.connect(registrationId);
    this.http.post(`${this.apiUrl}/registrations`, { eventId, registrationId }).pipe(
      takeUntilDestroyed(this.destroyRef),
    ).subscribe({
      error: () => {
        this.failRegistration('Registration failed. Please try again.');
        this.registrationEvents.disconnect();
      },
    });
  }

  private openPaymentWindow(payment: CheckoutPayment): void {
    this.errorMessage.set('');
    this.paymentStarted.set(true);

    try {
      this.paymentCheckout.openCheckout(payment, {
        onPaymentFailed: () => {
          this.paymentStarted.set(false);
          this.errorMessage.set('Your payment was not completed. You can try again.');
          this.registrationEvents.disconnect();
        },
      });
    } catch {
      this.paymentStarted.set(false);
      this.errorMessage.set('We could not open secure payment. Please try again.');
      this.registrationEvents.disconnect();
    }
  }

  private failRegistration(message: string): void {
    this.registrationInProgress.set(false);
    this.paymentStarted.set(false);
    this.errorMessage.set(message);
  }

  private readCheckoutPayment(): CheckoutPayment | null {
    const candidate = this.navigationState()?.['checkout'];
    return this.isCheckoutPayment(candidate) ? candidate : null;
  }

  private readEventCheckout(): EventCheckout | null {
    const candidate = this.navigationState()?.['eventCheckout'];
    if (typeof candidate !== 'object' || candidate === null) {
      return null;
    }

    const details = candidate as Record<string, unknown>;
    if (typeof details['eventId'] !== 'number'
      || !Number.isInteger(details['eventId'])
      || details['eventId'] <= 0
      || typeof details['eventName'] !== 'string'
      || details['eventName'].trim().length === 0
      || typeof details['eventDate'] !== 'string'
      || !Number.isFinite(Date.parse(details['eventDate']))) {
      return null;
    }

    return candidate as EventCheckout;
  }

  private navigationState(): Record<string, unknown> | null {
    const state = this.router.getCurrentNavigation()?.extras.state ?? window.history.state;
    return typeof state === 'object' && state !== null ? state as Record<string, unknown> : null;
  }

  private isCheckoutPayment(value: unknown): value is CheckoutPayment {
    if (typeof value !== 'object' || value === null) {
      return false;
    }

    const details = value as Record<string, unknown>;
    return typeof details['registrationId'] === 'string'
      && details['registrationId'].length > 0
      && typeof details['eventId'] === 'string'
      && details['eventId'].length > 0
      && ((typeof details['customerId'] === 'number'
        && Number.isFinite(details['customerId'])) || details['customerId'] === null)
      && typeof details['username'] === 'string'
      && typeof details['email'] === 'string'
      && typeof details['firstName'] === 'string'
      && typeof details['lastName'] === 'string'
      && typeof details['eventName'] === 'string'
      && details['eventName'].trim().length > 0
      && typeof details['eventDate'] === 'string'
      && Number.isFinite(Date.parse(details['eventDate']))
      && typeof details['amount'] === 'number'
      && Number.isFinite(details['amount'])
      && details['amount'] >= 0
      && typeof details['currency'] === 'string'
      && /^[A-Z]{3}$/.test(details['currency'])
      && typeof details['orderId'] === 'string'
      && details['orderId'].length > 0;
  }
}
