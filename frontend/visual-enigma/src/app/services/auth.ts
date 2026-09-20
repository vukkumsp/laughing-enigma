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
  accessToken: string | null;
}

export interface RefreshResponse {
  message: string;
  accessToken: string | null;
}

@Service()
export class Auth {

    private readonly apiUrl =  environment.apiUrl;

    private readonly http = inject(HttpClient);

    login(request: LoginRequest): Observable<LoginResponse> {
        return this.http.post<LoginResponse>(
        `${this.apiUrl}/auth/login`,
        request,
        {
            headers: {
                'X-Skip-Auth': 'true'
            },
            withCredentials: true
        }
        );
    }

    logout(): void {
        this.clearTokens()
    }

    isAuthenticated(): boolean {
        const accessToken = this.getAccessToken();

        if (!accessToken) {
            return false;
        }

        const tokenParts = accessToken.split('.');

        if (tokenParts.length !== 3) {
            return false;
        }

        try {
            const payload = JSON.parse(atob(tokenParts[1].replace(/-/g, '+').replace(/_/g, '/')));
            return typeof payload.exp !== 'number' || payload.exp > Math.floor(Date.now() / 1000);
        } catch {
            return false;
        }
    }

    refreshAccessToken(): Observable<RefreshResponse> {
        return this.http.post<RefreshResponse>(
            `${this.apiUrl}/auth/refresh`,
            {},
            {
                headers: {
                    'X-Skip-Auth': 'true'
                },
                withCredentials: true
            }
        );
    }

    // Token management methods
    private accessToken: string | null = null;

    storeAccessToken(accessToken: string): void {
        this.accessToken = accessToken;
    }

    getAccessToken(): string | null {
        return this.accessToken;
    }

    private clearAccessToken(): void {
        this.accessToken = null;
    }

    private clearTokens(): void {
        this.clearAccessToken();
    }
}
