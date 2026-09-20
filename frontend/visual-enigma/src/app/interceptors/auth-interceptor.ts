import { HttpInterceptorFn, HttpRequest } from '@angular/common/http';
import { Auth } from '../services/auth';
import { inject } from '@angular/core';
import { catchError, switchMap, throwError } from 'rxjs';
import { Router } from '@angular/router';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const router = inject(Router);
  const auth = inject(Auth);

  const isAuthEndpoint = req.url.endsWith('/auth/login') ||
    req.url.endsWith('/auth/refresh');

  if (req.headers.has('X-Skip-Auth') || isAuthEndpoint) {
    return next(req);
  }

  const refreshAndRetry = (request: HttpRequest<unknown>, originalError?: unknown) =>
    auth.refreshAccessToken().pipe(
      switchMap(response => {
        if (!response.accessToken) {
          return throwError(() => originalError ?? new Error('Unable to refresh access token'));
        }

        auth.storeAccessToken(response.accessToken);

        return next(request.clone({
          setHeaders: {
            Authorization: `Bearer ${response.accessToken}`
          }
        }));
      }),
      catchError(refreshError => {
        auth.logout();
        router.navigate(['/login']);

        return throwError(() => refreshError);
      })
    );

  const token = auth.getAccessToken();

  if (!token) {
    return refreshAndRetry(req);
  }

  const authReq = req.clone({
    setHeaders: {
      Authorization: `Bearer ${token}`
    }
  });
  
  return next(authReq).pipe(
    catchError(error => {

      if (error.status !== 401) {
        return throwError(() => error);
      }

      return refreshAndRetry(req, error);
    })
  );
};
