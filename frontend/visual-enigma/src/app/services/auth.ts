import { HttpClient } from '@angular/common/http';
import { inject, Service } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface LoginRequest {
  username: string;
  password: string;
}

export interface LoginResponse {
  message: string;
  token: string | null;
}

@Service()
export class Auth {

    private readonly apiUrl =  environment.apiUrl;

    private readonly http = inject(HttpClient);

    login(request: LoginRequest): Observable<LoginResponse> {
        return this.http.post<LoginResponse>(
        `${this.apiUrl}/auth/login`,
        request
        );
    }

    logout(): void {
        this.clearToken();
    }

    isAuthenticated(): boolean {
        return this.getToken() !== null;
    }

    private readonly tokenKey = 'access_token';

    storeToken(token: string): void {
        localStorage.setItem(this.tokenKey, token);
    }

    getToken(): string | null {
        return localStorage.getItem(this.tokenKey);
    }

    private clearToken(): void {
        localStorage.removeItem(this.tokenKey);
    }
}
