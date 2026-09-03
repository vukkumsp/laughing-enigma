import { inject, Service } from '@angular/core';
import { Observable, Subject } from 'rxjs';
import { environment } from '../../environments/environment';
import { fetchEventSource } from '@microsoft/fetch-event-source';
import { Auth } from './auth';

@Service()
export class RegistrationEventsService {

    private readonly apiUrl = environment.apiUrl;
    private readonly auth = inject(Auth);
    private controller?: AbortController;
    private readonly eventsSubject = new Subject<SseEvent>();

readonly events$ =
  this.eventsSubject.asObservable();

    connect(registrationId: string): void {

        // Close an existing connection if there is one
        this.disconnect();

        this.controller = new AbortController();
        const token = this.auth.getAccessToken();

        fetchEventSource(
            `${this.apiUrl}/registrations/${registrationId}/events`,
            {
                method: 'GET',

                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Accept': 'text/event-stream'
                },

                signal: this.controller.signal,

                async onopen(response) {
                    console.log(
                        'SSE connection opened:',
                        response.status
                    );
                },

                onmessage: (event) =>  {
                    console.log(
                        'SSE event:',
                        event.event,
                        event.data
                    );
                    console.log('SSE event name:', event.event);
                    console.log('SSE data:', event.data);

                    const data = JSON.parse(event.data);
                    
                    this.eventsSubject.next({
                        type: event.event,
                        data
                    });
                    switch (event.event) {
                        case 'PAYMENT_REQUIRED':
                            console.log('Received PAYMENT_REQUIRED event:', data);
                            break;
                        case 'PAYMENT_SUCCESS':
                            console.log('Received PAYMENT_SUCCESS event:', data);
                            break;
                        case 'PAYMENT_FAILED':
                            console.log('Received PAYMENT_FAILED event:', data);
                            break;
                        case 'TEST':
                            console.log('Received TEST event:', data);
                            break;

                        default:
                            console.warn('Unknown SSE event:', event.event);
                    }
                },

                onerror(error) {
                    if (error instanceof DOMException && error.name === 'AbortError') {
                        console.log('SSE connection aborted');
                        return;
                    }

                    console.error('SSE error:', error);
                    throw error;
                },

                onclose() {
                    console.log('SSE connection closed');
                }
            }
        );
    }

    disconnect(): void {

        if (this.controller) {
            console.log('Aborting SSE connection');

            this.controller.abort();
            this.controller = undefined;
        }
    }
}

export interface SseEvent {
  type: string;
  data: any;
}