import { Component } from '@angular/core';
import { Globals } from 'global';
import { Router } from '@angular/router';

@Component({
  selector: 'app-sidebar',
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.scss'],
  host:{
    class:'fixed-right-sidebar'
  }
})
export class SidebarComponent {

  constructor() {
  }
}