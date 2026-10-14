export interface MyRegistrationsResponse {
  customerUsername: string;
  registrations: MyRegistration[];
}

export interface MyRegistration {
  customerUsername: string;
  registrationId: string;
  orderId: string;
  bookingStatus: 'CONFIRMED' | 'CANCELLED' | 'PENDING';
  eventId: number;
  eventName: string;
  eventDescription: string;
  eventDate: string;
  availableSeats: number;
  currency: string;
  amount: number;
}
