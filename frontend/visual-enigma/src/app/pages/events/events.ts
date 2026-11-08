import { HttpClient } from '@angular/common/http';
import { Component, inject, OnInit, signal } from '@angular/core';
import { Router } from '@angular/router';
import { environment } from '../../../environments/environment';
import { EventCard } from './event-card';
import { Event } from '../../models/event.models';

@Component({
  selector: 'app-events',
  imports: [EventCard],
  templateUrl: './events.html',
  styleUrl: './events.scss',
})
export class Events implements OnInit {

  private readonly http = inject(HttpClient);
  private readonly router = inject(Router);
  
  private readonly apiUrl = environment.apiUrl;

  readonly events = signal<Event[]>([]);
  readonly loading = signal(true);
  readonly errorMessage = signal('');
  readonly registrationMessage = signal('');
  readonly registrationError = signal(false);
  readonly registeringEventId = signal<number | null>(null);

  ngOnInit() {
    this.loadEvents();
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
    const event = this.events().find(candidate => candidate.id === eventId);

    if (!event) {
      this.registeringEventId.set(null);
      return;
    }

    if (this.registeringEventId() !== null || event?.availableSeats === 0) {
      return;
    }

    this.registrationMessage.set('');
    this.registrationError.set(false);
    this.registeringEventId.set(eventId);

    void this.router.navigate(['/checkout'], {
      state: {
        eventCheckout: {
          eventId: event.id,
          eventName: event.name,
          eventDate: event.eventDate,
        },
      },
    }).then(navigated => {
      if (!navigated) {
        this.registrationError.set(true);
        this.registrationMessage.set('Unable to open checkout. Please try again.');
      }
    }).catch(() => {
      this.registrationError.set(true);
      this.registrationMessage.set('Unable to open checkout. Please try again.');
    }).finally(() => {
      this.registeringEventId.set(null);
    });
  }
}