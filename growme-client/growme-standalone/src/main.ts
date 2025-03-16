import { bootstrapApplication, platformBrowser } from '@angular/platform-browser';
import { AppComponent } from './app/app.component';
import { provideRouter } from '@angular/router';
import { routes } from './app/app.routes';
import {provideAnimations} from '@angular/platform-browser/animations'
import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { KeycloakService } from './app/services/keycloak.service';

const keycloakService = new KeycloakService();

keycloakService.init().then(() => {
  console.log('Keycloak initialized successfully. Bootstrapping Angular...');

  bootstrapApplication(AppComponent, {
    providers: [
        provideAnimations(),
        provideHttpClient(withInterceptorsFromDi()),
        provideRouter(routes),
        { provide: KeycloakService, useValue: keycloakService }
    ]
}).catch(err => console.error('Angular bootstrapping failed:', err));

}).catch(err => console.error('Keycloak initialization failed:', err));
