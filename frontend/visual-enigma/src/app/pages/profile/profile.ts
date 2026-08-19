import { HttpClient } from '@angular/common/http';
import { Component, inject, signal } from '@angular/core';

@Component({
  selector: 'app-profile',
  imports: [],
  templateUrl: './profile.html',
  styleUrl: './profile.scss',
})
export class Profile {

  private readonly http = inject(HttpClient);
  customer = signal<Record<string, string> | null>(null);
  loading = signal(true);
  errorMessage = signal('');

  constructor() {
    this.callCustomerMe();
  }

  callCustomerMe(){
    this.http.get<Record<string, string>>('http://localhost:8080/customers/me').subscribe({
      next: (response) => {
        this.customer.set(response);
        this.loading.set(false);
        console.log('Customer me response:', response);
      },
      error: (error) => {
        this.loading.set(false);
        this.errorMessage.set('We could not load your profile. Please try again later.');
        console.error('Error calling customer me endpoint:', error);
      }
    });
  }
}
