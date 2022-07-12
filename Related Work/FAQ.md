# FAQ

- Was ist ein Vokabular?
    - In some applications, especially in [logic](https://en.wikipedia.org/wiki/Logic), the alphabet is also known as the *vocabulary* and words are known as *formulas* or *sentences*; this breaks the letter/word metaphor and replaces it by a word/sentence metaphor. [REF](https://en.wikipedia.org/wiki/Formal_language)
- What is a “ground path rule”?
    - A rule that only contains constants, but no variables
- What is a body grounding?
    - It’s a specific instance of the body of a rule, aka. a collection of facts
- How is the confidence of a rule calculated?
    - The confidence of a rule is usually defined as number of body groundings, divided by the number of those body groundings that make the head true.
    - AnyBURL approximates: To compute the exact confidence can be costly, because it requires to do many joins depending on the number of body atoms. For that reason we only sample body groundings, for which we compute the respective head groundings. The computed confidence is thus an approximation of the correct confidence.
- What is noisy data?
    - [https://en.wikipedia.org/wiki/Noisy_data](https://en.wikipedia.org/wiki/Noisy_data)
- How does Noisy-Or aggregation work exactly? Doesn’t that wrongly lead to small probabilities? E.g. 0,9 * 0,9 * 0,9 = 0,7, while it should be higher instead of smaller?
