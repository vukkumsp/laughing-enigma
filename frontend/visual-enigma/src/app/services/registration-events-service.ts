import { inject, Service } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { fetchEventSource } from '@microsoft/fetch-event-source';
import { Auth } from './auth';

@Service()
export class RegistrationEventsService {

    private readonly apiUrl = environment.apiUrl;
    private readonly auth = inject(Auth);
    private controller?: AbortController;

    connect(registrationId: string): void {

        // Close an existing connection if there is one
        this.disconnect();

        const controller = new AbortController();
        const token = this.auth.getAccessToken();

        fetchEventSource(
            `${this.apiUrl}/registrations/${registrationId}/events`,
            {
                method: 'GET',

                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Accept': 'text/event-stream'
                },

                signal: controller.signal,

                async onopen(response) {
                    console.log(
                        'SSE connection opened:',
                        response.status
                    );
                },

                onmessage(event) {
                    console.log(
                        'SSE event:',
                        event.event,
                        event.data
                    );
                    console.log('SSE event name:', event.event);
                    console.log('SSE data:', event.data);

                    if (event.event === 'TEST') {
                        const data = JSON.parse(event.data);

                        console.log(
                            'Received TEST event:',
                            data
                        );
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
