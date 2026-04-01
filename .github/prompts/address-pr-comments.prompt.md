---
mode: agent
tools: [ 'github', 'atlassian', 'context7' ]
description: 'Review and address comments on a pull request implementing a Jira story'
---

Review the latest PR comments that have come in:

1. Evaluate and prioritize them
    * Determine if they are valid and worth addressing, or if they can be dismissed.

2. Make the code changes needed to address valid comments.
    * Do NOT modify any instruction or prompt files in this step.

3. Identify any instruction or prompt files that may have contributed to the issues found.
   Draft proposed changes to those files, but do NOT apply or save the changes yet.
   Note what each proposed change addresses and why.

4. Run impacted tests.

5. Present a summary of all pending changes:
    * Code changes made in step 2
    * Proposed instruction/prompt changes drafted in step 3
      Prompt for permission to commit and push the CODE CHANGES ONLY.
      Wait for explicit approval before proceeding. Do not commit anything else.

6. After pushing code, present the proposed instruction/prompt changes for review.
   List each file and the specific changes proposed.
   Wait for explicit approval before applying or committing any of them.
   Instruction and prompt files must NEVER be committed without a separate,
   explicit confirmation — independent of any prior code commit approval.

7. After pushing code, respond to pull request comments.

8. Re-request review from Copilot.