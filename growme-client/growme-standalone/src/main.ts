import { bootstrapApplication } from '@angular/platform-browser';
import { AppComponent } from './app/app.component';
import { provideRouter } from '@angular/router';
import { routes } from './app/app.routes';
import { provideAnimations } from '@angular/platform-browser/animations';
import { provideHttpClient, withFetch, withInterceptorsFromDi } from '@angular/common/http';
import { KeycloakService } from './app/services/keycloak.service';
import { provideToastr } from 'ngx-toastr';
import { appConfig } from './app/app.config';

bootstrapApplication(AppComponent, appConfig)
  .then(appRef => {
    console.log('Angular app bootstrapped successfully');
    // Initialize Keycloak after the app is bootstrapped
    const keycloakService = appRef.injector.get(KeycloakService);
    return keycloakService.init();
  })
  .catch(err => {
    console.error('Bootstrapping or Keycloak initialization failed:', err);
  });
