import { HttpClient } from '@angular/common/http';
import { Component, inject, signal } from '@angular/core';
import { environment } from '../../../environments/environment';
import { EventCard } from './event-card';

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
export class Events {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = environment.apiUrl;

  readonly events = signal<Event[]>([]);
  readonly loading = signal(true);
  readonly errorMessage = signal('');
  readonly registrationMessage = signal('');
  readonly registrationError = signal(false);
  readonly registeringEventId = signal<number | null>(null);

  constructor() {
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
    if (this.registeringEventId() !== null) {
      return;
    }

    this.registrationMessage.set('');
    this.registrationError.set(false);
    this.registeringEventId.set(eventId);

    this.http.post(`${this.apiUrl}/registrations`, { eventId }).subscribe({
      next: () => {
        this.events.update(events => events.map(event =>
          event.id === eventId
            ? { ...event, availableSeats: Math.max(0, event.availableSeats - 1) }
            : event
        ));
        this.registrationMessage.set('You are registered for the event.');
        this.registeringEventId.set(null);
      },
      error: () => {
        this.registrationError.set(true);
        this.registrationMessage.set('Registration failed. Please try again.');
        this.registeringEventId.set(null);
      }
    });
  }
}