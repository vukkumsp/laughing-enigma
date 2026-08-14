import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';

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

  constructor(private fb: FormBuilder, private router: Router) {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]]
    });
  }

  get email() {
    return this.loginForm.get('email');
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
    
    // Simulate API call
    setTimeout(() => {
      this.loading = false;
      
      // Simulate login success/failure (for demonstration)
      // In real scenario, this would depend on API response
      const email = this.loginForm.get('email')?.value;
      const password = this.loginForm.get('password')?.value;
      
      // Hardcoded credentials for demo (replace with actual API call)
      const isValidLogin = email === 'admin@example.com' && password === 'password';
      
      if (isValidLogin) {
        console.log('Login successful:', this.loginForm.value);
        this.router.navigate(['/']); // Navigate to home on successful login
      } else {
        console.log('Login failed:', this.loginForm.value);
        this.errorMessage = 'Invalid email or password. Please try again.';
        this.router.navigate(['/forbidden']); // Navigate to forbidden on failed login
      }
    }, 2000);
  }

  onReset() {
    this.submitted = false;
    this.loginForm.reset();
    this.errorMessage = null;
  }
}
