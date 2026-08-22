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
  refreshToken: string | null;
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
        request
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
    const refreshToken = this.getRefreshToken();

        return this.http.post<RefreshResponse>(
            `${this.apiUrl}/auth/refresh`,
            { refreshToken },
            {
                headers: {
                    'X-Skip-Auth': 'true'
                }
            }
        );
    }

    // Token management methods
    private readonly accessTokenKey = 'access_token';
    private readonly refreshTokenKey = 'refresh_token';

    storeAccessToken(accessToken: string): void {
        localStorage.setItem(this.accessTokenKey, accessToken);
    }

    getAccessToken(): string | null {
        return localStorage.getItem(this.accessTokenKey);
    }

    private clearAccessToken(): void {
        localStorage.removeItem(this.accessTokenKey);
    }

    storeRefreshToken(refreshToken: string): void {
        localStorage.setItem(this.refreshTokenKey, refreshToken);
    }

    getRefreshToken(): string | null {
        return localStorage.getItem(this.refreshTokenKey);
    }

    private clearRefreshToken(): void {
        localStorage.removeItem(this.refreshTokenKey);
    }

    private clearTokens(): void {
        this.clearAccessToken();
        this.clearRefreshToken();
    }
}
