import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { AESTestComponent } from './Pages/aes-test/aes-test.component';
import { RsaTestComponent } from './Pages/rsa-test/rsa-test.component';

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
    path: '',
    redirectTo: '/login',
    pathMatch: 'full'
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes, { useHash: true })],
  exports: [RouterModule]
})

export class AppRoutingModule { }
