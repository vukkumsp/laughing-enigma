export interface Event {
  id: number;
  eventId: number;
  name: string;
  description: string;
  eventDate: string;
  availableSeats: number;
  currency: string;
  price: number;
}

interface DetailedEventContent extends Event {
  category: string;
  bannerImageUrl: string;
  duration: string;
  ticketPrice: number;
  host: {
    name: string;
    title: string;
    imageUrl: string;
    bio: string;
  };
  highlights: string[];
  agenda: {
    time: string;
    title: string;
    description: string;
  }[];
}

export interface PhysicalVenue {
  type: 'in-person';
  name: string;
  address: string;
  timezone: string;
}

export interface OnlineVenue {
  type: 'online';
  platform: string;
  timezone: string;
  accessInstructions: string;
}

export interface HybridVenue {
  type: 'hybrid';
  inPerson: PhysicalVenue;
  online: OnlineVenue;
}

export type EventVenue = PhysicalVenue | OnlineVenue | HybridVenue;

export type DetailedEvent = DetailedEventContent & {
  venue: EventVenue;
};
