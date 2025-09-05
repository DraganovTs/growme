export interface UserProfile {
    userId: string;
  username: string;
  email: string;
  roles: string[];
  profileComplete?: boolean;
}