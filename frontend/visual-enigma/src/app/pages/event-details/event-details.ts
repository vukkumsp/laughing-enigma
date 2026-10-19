import { HttpClient } from '@angular/common/http';
import { DatePipe } from '@angular/common';
import { Component, inject, OnInit, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { combineLatest } from 'rxjs';
import { DetailedEvent } from '../../models/event.models';

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
}
