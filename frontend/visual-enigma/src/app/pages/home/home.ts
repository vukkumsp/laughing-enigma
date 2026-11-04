import { HttpClient } from '@angular/common/http';
import { Component, inject, OnInit, signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { environment } from '../../../environments/environment';
import { MyRegistration } from '../../services/my-registrations-service/my-registrations.models';
import { MyRegistrationsService } from '../../services/my-registrations-service/my-registrations-service';

export interface CurrentUser {
  customerId: number;
  firstName: string;
  lastName: string;
  username: string;
}

interface HomeEvent {
  registration: MyRegistration;
  registrationId: string;
  name: string;
  date: string;
  month: string;
  day: string;
  status: string;
}

type HomeTab = 'registered' | 'payment-pending' | 'cancelled';

@Component({
  selector: 'app-home',
  imports: [],
  templateUrl: './home.html',
  styleUrl: './home.scss',
})
export class Home implements OnInit {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = environment.apiUrl;
  private readonly myRegistrationsService = inject(MyRegistrationsService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);

  readonly activeTab = signal<HomeTab>('registered');
  readonly currentUser = signal<CurrentUser | null>(null);
  readonly userLoading = signal(true);
  readonly userError = signal('');
  readonly registrationsError = signal('');

  ngOnInit(): void {
    if (this.route.snapshot.queryParamMap.get('tab') === 'payment-pending') {
      this.activeTab.set('payment-pending');
    }

    this.loadCurrentUser();
    this.loadMyRegistrations();
  }

  readonly registeredEvents = signal<HomeEvent[]>([]);
  readonly paymentPendingEvents = signal<HomeEvent[]>([]);
  readonly cancelledEvents = signal<HomeEvent[]>([]);

  private loadCurrentUser(): void {
    this.http.get<CurrentUser>(`${this.apiUrl}/customers/me`).subscribe({
      next: (user) => {
        this.currentUser.set(user);
        this.userLoading.set(false);
        console.log('Current user loaded:', user);
      },
      error: () => {
        this.userLoading.set(false);
        this.userError.set('We could not load your account details.');
      },
    });
  }

  private loadMyRegistrations(): void {
    this.myRegistrationsService.getMyRegistrations().subscribe({
      next: (response) => {
        this.registeredEvents.set(response.registrations
          .filter((registration) => registration.bookingStatus === 'CONFIRMED')
          .map((registration) => this.toHomeEvent(registration, 'Confirmed')));
        this.paymentPendingEvents.set(response.registrations
          .filter((registration) => registration.bookingStatus === 'PENDING')
          .map((registration) => this.toHomeEvent(registration, 'Payment pending')));
        this.cancelledEvents.set(response.registrations
          .filter((registration) => registration.bookingStatus === 'CANCELLED')
          .map((registration) => this.toHomeEvent(registration, 'Cancelled')));
      },
      error: () => {
        this.registrationsError.set('We could not load your event registrations.');
      },
    });
  }

  private toHomeEvent(registration: MyRegistration, status: string): HomeEvent {
    const eventDate = new Date(registration.eventDate);

    return {
      registration,
      registrationId: registration.registrationId,
      name: registration.eventName,
      date: eventDate.toLocaleDateString('en-US', {
        year: 'numeric',
        month: 'long',
        day: '2-digit',
      }),
      month: eventDate.toLocaleDateString('en-US', { month: 'short' }),
      day: eventDate.toLocaleDateString('en-US', { day: '2-digit' }),
      status,
    };
  }

  payNow(event: HomeEvent): void {
    const user = this.currentUser();
    const registration = event.registration;

    void this.router.navigate(['/checkout'], {
      state: {
        checkoutOrigin: 'payment-pending',
        checkout: {
          registrationId: registration.registrationId,
          eventId: String(registration.eventId),
          customerId: user?.customerId ?? null,
          username: registration.customerUsername,
          email: '',
          firstName: user?.firstName ?? '',
          lastName: user?.lastName ?? '',
          eventName: registration.eventName,
          eventDate: registration.eventDate,
          amount: registration.amount,
          currency: registration.currency,
          orderId: registration.orderId,
        },
      },
    }).then(navigated => {
      if (!navigated) {
        this.registrationsError.set('Unable to open checkout. Please try again.');
      }
    }).catch(() => {
      this.registrationsError.set('Unable to open checkout. Please try again.');
    });
  }

  selectTab(tab: HomeTab): void {
    this.activeTab.set(tab);
  }
}