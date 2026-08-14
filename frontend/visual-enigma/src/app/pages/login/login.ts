import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';

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

  constructor(private fb: FormBuilder) {
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
      // Handle login logic here
      console.log('Login attempt with:', this.loginForm.value);
    }, 2000);
  }

  onReset() {
    this.submitted = false;
    this.loginForm.reset();
    this.errorMessage = null;
  }
}
