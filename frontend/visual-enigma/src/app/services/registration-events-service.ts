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
    private readonly connectionEndedSubject = new Subject<void>();

readonly events$ =
  this.eventsSubject.asObservable();
    readonly connectionEnded$ = this.connectionEndedSubject.asObservable();

    connect(registrationId: string): void {

        // Close an existing connection if there is one
        this.disconnect();

        const controller = new AbortController();
        this.controller = controller;
        const token = this.auth.getAccessToken();

        void fetchEventSource(
            `${this.apiUrl}/registrations/${registrationId}/events`,
            {
                method: 'GET',

                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Accept': 'text/event-stream'
                },

                signal: controller.signal,

                onopen: async (response) => {
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

                onerror: (error) => {
                    if (error instanceof DOMException && error.name === 'AbortError') {
                        console.log('SSE connection aborted');
                        this.endConnection(controller);
                        return;
                    }

                    console.error('SSE error:', error);
                    throw error;
                },

                onclose: () => {
                    console.log('SSE connection closed');
                    this.endConnection(controller);
                }
            }
        ).catch((error: unknown) => {
            this.endConnection(controller);
            if (!(error instanceof DOMException && error.name === 'AbortError')) {
                console.error('SSE connection failed:', error);
            }
        });
    }

    disconnect(): void {

        if (this.controller) {
            const controller = this.controller;
            console.log('Aborting SSE connection');

            this.controller = undefined;
            this.connectionEndedSubject.next();
            controller.abort();
        }
    }

    private endConnection(controller: AbortController): void {
        if (this.controller !== controller) {
            return;
        }

        this.controller = undefined;
        this.connectionEndedSubject.next();
    }
}

export interface SseEvent {
  type: string;
  data: any;
}