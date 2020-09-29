The `five_letter_words.txt` file needs to be in this directory for student-submitted code to read from while being tested. This is because the starter code for the assignment includes a `makeDictionary` method with this line in it: 

```
infile = new Scanner (new File("five_letter_words.txt"));
```

That path is relative to the working directory, which in this case is the folder this README is in.
