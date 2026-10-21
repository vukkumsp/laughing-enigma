import { HttpClient } from '@angular/common/http';
import { DatePipe } from '@angular/common';
import { Component, inject, OnInit, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { combineLatest } from 'rxjs';
import { DetailedEvent, EventVenue, OnlineVenue, PhysicalVenue } from '../../models/event.models';

interface VenueDisplay {
  type: 'in-person' | 'online';
  heading: string;
  name: string;
  location: string;
  timezone: string;
  accessInstructions?: string;
}

@Component({
  selector: 'app-event-details',
  imports: [DatePipe, RouterLink],
  templateUrl: './event-details.html',
  styleUrl: './event-details.scss',
})
export class EventDetails implements OnInit {
  private readonly http = inject(HttpClient);
  private readonly route = inject(ActivatedRoute);

  readonly loading = signal(true);
  readonly errorMessage = signal('');
  readonly event = signal<DetailedEvent | null>(null);

  venueFormat(venue: EventVenue): string {
    switch (venue.type) {
      case 'in-person':
        return 'In person';
      case 'online':
        return 'Online';
      case 'hybrid':
        return 'Hybrid';
    }
  }

  venueDetails(venue: EventVenue): VenueDisplay[] {
    switch (venue.type) {
      case 'in-person':
        return [this.toPhysicalVenueDisplay(venue)];
      case 'online':
        return [this.toOnlineVenueDisplay(venue)];
      case 'hybrid':
        return [
          this.toPhysicalVenueDisplay(venue.inPerson),
          this.toOnlineVenueDisplay(venue.online),
        ];
    }
  }

  ngOnInit(): void {
    combineLatest({
      events: this.http.get<DetailedEvent[]>('/assets/event-details.json'),
      params: this.route.paramMap,
    }).subscribe({
      next: ({ events, params }) => {
        const eventId = Number(params.get('id'));
        this.event.set(events.find(sampleEvent => sampleEvent.id === eventId) ?? null);
        this.loading.set(false);
      },
      error: () => {
        this.errorMessage.set('We could not load this event. Please try again later.');
        this.loading.set(false);
      },
    });
  }

  private toPhysicalVenueDisplay(venue: PhysicalVenue): VenueDisplay {
    return {
      type: 'in-person',
      heading: 'In person',
      name: venue.name,
      location: venue.address,
      timezone: venue.timezone,
    };
  }

  private toOnlineVenueDisplay(venue: OnlineVenue): VenueDisplay {
    return {
      type: 'online',
      heading: 'Online',
      name: venue.platform,
      location: '',
      timezone: venue.timezone,
      accessInstructions: venue.accessInstructions,
    };
  }
}
