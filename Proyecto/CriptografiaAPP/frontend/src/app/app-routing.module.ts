import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { AESTestComponent } from './Pages/aes-test/aes-test.component';
import { RsaTestComponent } from './Pages/rsa-test/rsa-test.component';
import { HomeComponent } from './Pages/home/home.component';
import { KemTestComponent } from './Pages/kem-test/kem-test.component';
import { AlgoritmosComponent } from './Pages/algoritmos/algoritmos.component';

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
    path: 'Kem/:algoritmo',
    component: KemTestComponent
  },
  {
    path: 'algoritmos',
    component: AlgoritmosComponent
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
