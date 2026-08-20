import { HttpInterceptorFn } from '@angular/common/http';
import { Auth } from '../services/auth';
import { inject } from '@angular/core';
import { catchError, switchMap, throwError } from 'rxjs';
import { Router } from '@angular/router';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const router = inject(Router);
  const auth = inject(Auth);

  if (req.headers.has('X-Skip-Auth')) {
    return next(req);
  }

  const token = auth.getAccessToken();

  if (!token) {
    return next(req);
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

      // Don't attempt to refresh the refresh request itself
      if (req.url.endsWith('/auth/refresh')) {
        auth.logout();
        router.navigate(['/login']);

        return throwError(() => error);
      }

      const refreshToken = auth.getRefreshToken();

      if (!refreshToken) {
        auth.logout();
        router.navigate(['/login']);

        return throwError(() => error);
      }

      return auth.refreshAccessToken().pipe(
        switchMap(response => {

          if (!response.accessToken) {
            auth.logout();
            router.navigate(['/login']);

            return throwError(() => error);
          }

          auth.storeAccessToken(response.accessToken);

          const retryRequest = req.clone({
            setHeaders: {
              Authorization: `Bearer ${response.accessToken}`
            }
          });


          console.log('Retrying request with new access token:', retryRequest);

          return next(retryRequest);
        }),
        catchError(refreshError => {

          auth.logout();
          router.navigate(['/login']);

          return throwError(() => refreshError);
        })
      );
    })
  );
};
