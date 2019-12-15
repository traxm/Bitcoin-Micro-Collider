# Bitcoin Micro Collider

Bitcoin Micro Collider is a bitcoin address/key generator which compares randomly generated address/key combinations against a list of addresses provided by the user.

For example, if a user supplied a list of all bitcoin addresses containing bitcoin balances, the program would generate random address/key combinations until a match was found against an address containing a balance.  The program would alert the user and save the address/key combination to a text file for future use.  The user would have the option to utilize the private key to take control of the address and it's underlying holdings.


# How to use Bitcoin Micro Collider

Step 0: **Install a Java runtime environment on your computer**.  If you don't already have this installed, a Java runtime can be downloaded from ninite.com (for example Java AdoptOpenJDK).

Step 1: **Download the Bitcoin Micro Collider executable JAR file [here](https://github.com/traxm/Bitcoin-Micro-Collider/releases)**.

Step 2: **Create or download a list of bitcoin addresses to scan**.  The file needs to be in text format with each address on a separate line.  Example file [here](https://github.com/traxm/Bitcoin-Micro-Collider/tree/master/example%20address%20lists).

Step 3: **Open the executable JAR file** called BitcoinMicroCollider.jar

Step 4: **Click "Load Address File"** and browse to the bitcoin address text file

Step 5: Wait while the file is loaded (this can take some time for large files)

Step 6: After the text file has been loaded, the **"Start Collider"** button will turn green.  Click this button to begin the collider process.
