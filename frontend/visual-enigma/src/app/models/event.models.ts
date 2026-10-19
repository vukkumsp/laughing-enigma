export interface Event {
  id: number;
  name: string;
  description: string;
  eventDate: string;
  availableSeats: number;
}

interface DetailedEventContent extends Event {
  category: string;
  bannerImageUrl: string;
  duration: string;
  ticketPrice: string;
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
  name: string;
  address: string;
  timezone: string;
}

export interface OnlineVenue {
  platform: string;
  timezone: string;
  accessInstructions: string;
}

export type DetailedEvent = DetailedEventContent & (
  | { format: 'offline'; venue: PhysicalVenue }
  | { format: 'online'; venue: OnlineVenue }
  | {
      format: 'hybrid';
      venue: {
        inPerson: PhysicalVenue;
        online: OnlineVenue;
      };
    }
);
