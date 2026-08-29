import { Service } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment.production';

@Service()
export class RegistrationEventsService {

    private readonly apiUrl =  environment.apiUrl;

    connect(registrationId: string): Observable<MessageEvent> {

    return new Observable(observer => {

      const url =
        `${this.apiUrl}/registrations/${registrationId}/events`;

      const eventSource = new EventSource(url);

      eventSource.onopen = () => {
        console.log('SSE connection opened');
      };

      eventSource.onmessage = (event) => {
        observer.next(event);
      };

      eventSource.onerror = (error) => {
        console.error('SSE error:', error);
        observer.error(error);
        eventSource.close();
      };

      return () => {
        console.log('Closing SSE connection');
        eventSource.close();
      };
    });
  }
}
