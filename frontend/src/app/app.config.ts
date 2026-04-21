import { ApplicationConfig } from '@angular/core';
import { provideHttpClient } from '@angular/common/http';

// provideHttpClient habilita o HttpClient para todos os componentes
export const appConfig: ApplicationConfig = {
  providers: [provideHttpClient()]
};