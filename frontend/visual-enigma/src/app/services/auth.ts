import { HttpClient } from '@angular/common/http';
import { inject, Service } from '@angular/core';
import { Observable } from 'rxjs';

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

    private readonly apiUrl = 'http://localhost:8080';

    private readonly http = inject(HttpClient);

    login(request: LoginRequest): Observable<LoginResponse> {
        return this.http.post<LoginResponse>(
        `${this.apiUrl}/auth/login`,
        request
        );
    }

    private readonly tokenKey = 'access_token';

    storeToken(token: string): void {
        localStorage.setItem(this.tokenKey, token);
    }

    getToken(): string | null {
        return localStorage.getItem(this.tokenKey);
    }

    clearToken(): void {
        localStorage.removeItem(this.tokenKey);
    }
}
