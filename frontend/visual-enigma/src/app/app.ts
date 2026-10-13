import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { AppNavigation } from './app-navigation';

@Component({
  selector: 'app-root',
  imports: [AppNavigation, RouterOutlet],
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class App {
  protected readonly title = signal('visual-enigma');
}
