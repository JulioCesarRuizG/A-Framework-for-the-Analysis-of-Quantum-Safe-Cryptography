import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { AESTestComponent } from './Pages/aes-test/aes-test.component';
import { RsaTestComponent } from './Pages/rsa-test/rsa-test.component';
import { HomeComponent } from './Pages/home/home.component';
import { KyberTestComponent } from './Pages/kyber-test/kyber-test.component';
import { SikeTestComponent } from './Pages/sike-test/sike-test.component';
import { BikeTestComponent } from './Pages/bike-test/bike-test.component';

const routes: Routes = [
  {
    path: 'aes',
    component: AESTestComponent
  },
  {
    path: 'rsa',
    component: RsaTestComponent
  },
  {
    path: 'home',
    component: HomeComponent
  },
  {
    path: 'kyber',
    component: KyberTestComponent
  },
  {
    path: 'sike',
    component: SikeTestComponent
  },
  {
    path: 'bike',
    component: BikeTestComponent
  },
  {
    path: '',
    redirectTo: '/home',
    pathMatch: 'full'
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes, { useHash: true })],
  exports: [RouterModule]
})

export class AppRoutingModule { }
