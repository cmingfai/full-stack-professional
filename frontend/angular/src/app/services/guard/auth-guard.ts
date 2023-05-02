import {Router} from "@angular/router";
import {inject} from "@angular/core";
import {AccessGuardService} from "./access-guard.service";

export const authGuard = () => {
  console.log('authGuard#canActivate called');

  const router = inject(Router);
  const accessGuardService= inject(AccessGuardService);

  let loggedIn=accessGuardService.isLoggedIn();

  if (!loggedIn) {
    // Redirect to the login page
    router.navigate(["login"]);
    return false;
  }

  return true;

};
