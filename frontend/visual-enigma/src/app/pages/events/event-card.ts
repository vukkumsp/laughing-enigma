import { DatePipe } from '@angular/common';
import { Component, input, output } from '@angular/core';
import { RouterLink } from '@angular/router';
import { Event } from '../../models/event.models';

@Component({
  selector: 'app-event-card',
  imports: [DatePipe, RouterLink],
  templateUrl: './event-card.html',
  styleUrl: './event-card.scss',
})
export class EventCard {
  readonly event = input.required<Event>();
  readonly isRegistering = input(false);
  readonly registerEvent = output<number>();
}