import { Component } from '@angular/core';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  title = 'angular';
  clickCount: number=0;
  lastCreatedElement='';

  clickHandler() {
    this.clickCount++;
  }

  elementCreatedHandler(element: string) {
    this.lastCreatedElement=element;

  }
}
