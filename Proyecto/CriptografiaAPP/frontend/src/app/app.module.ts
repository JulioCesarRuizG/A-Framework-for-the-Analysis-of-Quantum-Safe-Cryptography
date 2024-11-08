import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { NgModule } from '@angular/core';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { SpinnerComponent } from './Components/spinner/spinner.component';
import { AESTestComponent } from './Pages/aes-test/aes-test.component';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';

import { MatIconModule } from '@angular/material/icon';
import { CanvasJSAngularChartsModule } from '@canvasjs/angular-charts';
import { RsaTestComponent } from './Pages/rsa-test/rsa-test.component';
import { HomeComponent } from './Pages/home/home.component';
import { SidebarComponent } from './Components/sidebar/sidebar.component';
import { KyberTestComponent } from './Pages/kyber-test/kyber-test.component';
import { SikeTestComponent } from './Pages/sike-test/sike-test.component';
import { BikeTestComponent } from './Pages/bike-test/bike-test.component';


@NgModule({
  declarations: [
    AppComponent,
    SpinnerComponent,
    SidebarComponent,
    AESTestComponent,
    RsaTestComponent,
    HomeComponent,
    KyberTestComponent,
    SikeTestComponent,
    BikeTestComponent
  ],
  imports: [
    CommonModule,
    BrowserModule,
    BrowserAnimationsModule,
    AppRoutingModule,
    HttpClientModule,
    FormsModule,
    ReactiveFormsModule,
    MatIconModule,
    CanvasJSAngularChartsModule
  ],
  providers: [{
    provide: 'ApiEndpoint', useValue: 'http://localhost:8080/criptografiaApp/webapi/CryptographyTest',
  }, provideAnimationsAsync()
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
