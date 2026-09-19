import { HttpClient } from '@angular/common/http';
import { Component, inject, OnInit, signal } from '@angular/core';
import { environment } from '../../../environments/environment';

export interface CurrentUser {
  firstName: string;
  lastName: string;
  username: string;
}

interface HomeEvent {
  id: number;
  name: string;
  date: string;
  month: string;
  day: string;
  venue: string;
  status?: string;
}

type HomeTab = 'registered' | 'payment-pending';

@Component({
  selector: 'app-home',
  imports: [],
  templateUrl: './home.html',
  styleUrl: './home.scss',
})
export class Home implements OnInit {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = environment.apiUrl;

  readonly activeTab = signal<HomeTab>('registered');
  readonly currentUser = signal<CurrentUser | null>(null);
  readonly userLoading = signal(true);
  readonly userError = signal('');

  ngOnInit(): void {
    this.loadCurrentUser();
  }

  // Placeholder data for the dashboard until the home API is available.
  readonly registeredEvents: HomeEvent[] = [
    {
      id: 1,
      name: 'Design Systems Summit',
      date: 'October 18, 2026',
      month: 'Oct',
      day: '18',
      venue: 'The Foundry, Austin',
      status: 'Confirmed',
    },
    {
      id: 2,
      name: 'Future of Product Night',
      date: 'November 06, 2026',
      month: 'Nov',
      day: '06',
      venue: 'Pier 27, San Francisco',
      status: 'Confirmed',
    },
  ];

  readonly paymentPendingEvents: HomeEvent[] = [
    {
      id: 3,
      name: 'Creative Technology Forum',
      date: 'December 02, 2026',
      month: 'Dec',
      day: '02',
      venue: 'The Glasshouse, New York',
      status: 'Payment pending',
    },
    {
      id: 4,
      name: 'Independent Makers Meetup',
      date: 'January 15, 2027',
      month: 'Jan',
      day: '15',
      venue: 'Assembly Hall, Chicago',
      status: 'Payment pending',
    },
  ];

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

  selectTab(tab: HomeTab): void {
    this.activeTab.set(tab);
  }
}