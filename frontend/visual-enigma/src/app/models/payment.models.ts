
export interface PaymentRequiredEvent {
  registrationId: string;
  paymentId: string;
  razorpayOrderId: string;
  amount: number;
  currency: string;
}

export interface PaymentSuccessEvent {
  registrationId: string;
  paymentId: string;
  razorpayPaymentId: string;
}

export interface PaymentFailedEvent {
  registrationId: string;
  paymentId: string;
  reason: string;
}