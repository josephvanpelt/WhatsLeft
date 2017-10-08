# WhatsLeft
Whats Left is a free-libre App for "anti-budgeting" (figuring out what's left in your account based on uncleared transactions).

License: GPL v3 ([https://www.gnu.org/licenses/gpl-3.0.en.html](https://www.gnu.org/licenses/gpl-3.0.en.html))
Coffee cup artwork license: Attribution CC BY ([https://creativecommons.org/licenses/](https://creativecommons.org/licenses/))

You can get the binary here: [https://play.google.com/store/apps/details?id=com.jvanpelt.whatsleft](https://play.google.com/store/apps/details?id=com.jvanpelt.whatsleft)
(If you really need the app and cannot afford to support me, please let me know.)

## Budgeting
I recommend that you create a budget, but this app can be helpful for times when you need to decide if you have enough money for an important investment (or maybe you didn't stick to the budget that well).

## Privacy
The app requires no extra permissions.  Nothing is sent out or shared (by design).  All the data entered in the app is only stored on the phone in a sqlite database.  The database is stored in the sandbox that android provides (encryption planned for future versions).

## How it works
1. On the first page you enter what is currently showing as your account balance.
2. Next you enter all your regular transactions (these are stored in the database so you can easily edit what has cleared in the future).
3. On the third page you can review which transactions have cleared (and make changes).
4. Finally you can see what's left in the account on the last page.
