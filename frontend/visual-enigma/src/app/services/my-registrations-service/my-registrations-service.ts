import { HttpClient } from '@angular/common/http';
import { inject, Service } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { MyRegistrationsResponse } from './my-registrations.models';

@Service()
export class MyRegistrationsService {
  private readonly apiUrl = environment.apiUrl;
  private readonly http = inject(HttpClient);

  getMyRegistrations(): Observable<MyRegistrationsResponse> {
    return this.http.post<MyRegistrationsResponse>(
      `${this.apiUrl}/me/registrations`,
      {}
    );
  }
}
