import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { Auth } from '../../services/auth';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './login.html',
  styleUrl: './login.scss',
})
export class Login {
  loginForm: FormGroup;
  submitted = false;
  loading = false;
  errorMessage: string | null = null;
  private readonly auth = inject(Auth);

  constructor(private fb: FormBuilder, private router: Router) {
    this.loginForm = this.fb.group({
      username: ['', [Validators.required]],
      password: ['', [Validators.required, Validators.minLength(6)]]
    });
  }

  get username() {
    return this.loginForm.get('username');
  }

  get password() {
    return this.loginForm.get('password');
  }

  onSubmit() {
    this.submitted = true;
    this.errorMessage = null;

    if (this.loginForm.invalid) {
      return;
    }

    this.loading = true;
    console.log('Login form value:', this.loginForm.value);

    this.auth.login({
      username: this.loginForm.value.username!,
      password: this.loginForm.value.password!
    }).subscribe({
      next: (response) => {
        if (response.accessToken && response.refreshToken) {
          this.auth.storeAccessToken(response.accessToken);
          this.auth.storeRefreshToken(response.refreshToken);
          this.router.navigate(['/profile']);
        } else {
          this.router.navigate(['/unauthorized']);
        }
      },
      error: (error) => {
        if (error.status === 401) {
          this.router.navigate(['/unauthorized']);
        }
      }
    });
    
    // Simulate API call
    // setTimeout(() => {
    //   this.loading = false;
      
    //   // Simulate login success/failure (for demonstration)
    //   // In real scenario, this would depend on API response
    //   const email = this.loginForm.get('email')?.value;
    //   const password = this.loginForm.get('password')?.value;
      
    //   // Hardcoded credentials for demo (replace with actual API call)
    //   const isValidLogin = email === 'admin@example.com' && password === 'password';
      
    //   if (isValidLogin) {
    //     console.log('Login successful:', this.loginForm.value);
    //     this.router.navigate(['/']); // Navigate to home on successful login
    //   } else {
    //     console.log('Login failed:', this.loginForm.value);
    //     this.errorMessage = 'Invalid email or password. Please try again.';
    //     this.router.navigate(['/unauthorized']); // Navigate to unauthorized on failed login
    //   }
    // }, 2000);
  }

  onReset() {
    this.submitted = false;
    this.loginForm.reset();
    this.errorMessage = null;
  }
}
