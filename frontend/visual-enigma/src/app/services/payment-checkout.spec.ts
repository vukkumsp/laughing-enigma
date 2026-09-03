import { TestBed } from '@angular/core/testing';

import { PaymentCheckout } from './payment-checkout';

describe('PaymentCheckout', () => {
  let service: PaymentCheckout;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(PaymentCheckout);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
