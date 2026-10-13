import { Component, DestroyRef, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { NavigationEnd, Router, RouterLink } from '@angular/router';
import { filter, startWith } from 'rxjs';

@Component({
  selector: 'app-navigation',
  imports: [RouterLink],
  templateUrl: './app-navigation.html',
  styleUrl: './app-navigation.scss'
})
export class AppNavigation {
  readonly currentRoute = signal('');

  private readonly router = inject(Router);
  private readonly destroyRef = inject(DestroyRef);

  constructor() {
    this.router.events
      .pipe(
        filter(event => event instanceof NavigationEnd),
        startWith(null),
        takeUntilDestroyed(this.destroyRef)
      )
      .subscribe(() => this.currentRoute.set(this.router.url.split(/[?#]/, 1)[0]));
  }

  isCurrentRoute(path: string): boolean {
    return this.currentRoute() === path;
  }
}
