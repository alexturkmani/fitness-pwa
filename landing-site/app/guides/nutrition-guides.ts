import type { Guide } from './content';

// All articles in this content batch share the requested publication date.
const publication = { publishedAt: '2026-10-11' };

export const nutritionGuides: Guide[] = [
  {
    ...publication,
    slug: 'raw-versus-cooked-food-weights',
    title: 'Raw versus cooked food weights: match the entry to the scale',
    metaTitle: 'Raw vs Cooked Food Weights for Logging',
    description: 'Avoid raw and cooked weight mix-ups with a rice example, entry checks and a practical method for logging your actual portion.',
    category: 'NUTRITION TRACKING', readTime: '5 min read',
    intro: 'You weighed rice after cooking, but the entry in your diary describes dry rice. Both numbers say grams, yet they describe different things. For home cooks who weigh ingredients, the important choice is matching the food entry to the state in which it was measured.',
    takeaway: 'Choose the entry and measurement together. Use your own batch weights when converting portions, and do not assume every food has the same cooking yield.',
    sections: [
      { id: 'food-state', title: 'Read the preparation words before entering grams', paragraphs: [
        'Look for dry, raw, boiled, drained, roasted or prepared in the food description. Those words establish what the nutrition values describe. A measured 150 grams of cooked rice belongs with a compatible cooked entry, rather than 150 grams of dry rice. If the description is ambiguous, check the package or another clearly described source before using it.',
        'Food Standards Australia New Zealand explains that cooking can change weight through water and fat gains or losses. That means weight change is not a universal conversion rule. Rice absorbing water and meat losing cooking juices require different reasoning. Keep edible portions separate from bones, skins or liquid you discard.',
      ] },
      { id: 'rice-example', title: 'Convert a cooked share using your own batch', paragraphs: [
        'Suppose you cook 200 grams of dry rice in water and the finished rice weighs 600 grams. You serve yourself 150 grams of that cooked batch. Your serving is one quarter of the finished rice, so it corresponds to one quarter of the original dry quantity: 50 grams. These are invented weights to demonstrate the method, not a standard rice yield.',
        'If you use the dry label for the calculation, apply its values to 50 grams. Alternatively, use a suitable cooked entry for the measured 150 grams. Choose one approach. Recording both would count the same rice twice, and adding oil or sauce requires its own accounting.',
      ] },
      { id: 'yield-limits', title: 'Know when the conversion stops being useful', paragraphs: [
        'The batch-share approach assumes the portion represents the batch reasonably well. It does not reconstruct the nutrition of discarded fat or cooking liquid. A drained meat dish, breaded food or mixed casserole may need a more specific prepared-food source. Avoid correcting every cooked ingredient with the rice ratio from another meal.',
        'For a repeat dish, record the measured starting and finished weights somewhere you can consult while calculating. If the next batch cooks longer and finishes lighter, update that calculation. Keeping yesterday’s cooked portion estimate while changing today’s cooking method can quietly introduce more error than rounding a final number.',
      ] },
      { id: 'logging-workflow', title: 'Make the method visible in your logging routine', paragraphs: [
        'Before logging dinner, identify the source, food state, edible quantity and any separately added ingredients. Calculate the portion using those details, then enter the resulting meal nutrition through Nexal’s free manual meal, calorie and macro logging on Android. Batch arithmetic can happen outside the app; this method does not depend on an automatic recipe converter.',
        'When copying a recent meal, check whether the measured state and portion still match. A useful final question is whether another person could tell what your quantity means from the source you used. Clear assumptions make later comparisons more useful than a long string of decimal places attached to an uncertain entry.',
      ] },
    ],
    feature: { href: '/calorie-macro-tracker', label: 'Explore manual nutrition tracking', text: 'Use Nexal’s free Android meal tracker for the portion you actually ate.' },
    sources: [{ href: 'https://www.foodstandards.gov.au/business/labelling/nutrition-panel-calculator/weight-change-factors', label: 'FSANZ: cooking and weight change factors' }],
    faqs: [
      { question: 'Should I always weigh food raw?', answer: 'No. Either state can work when the nutrition entry matches it. Raw and cooked quantities should not be exchanged without a suitable calculation.' },
      { question: 'Can I use one rice conversion for every batch?', answer: 'Treat your measured batch ratio as specific to that batch. Water absorption and cooking conditions can change the finished weight.' },
    ],
  },
  {
    ...publication,
    slug: 'grams-versus-servings-food-logging',
    title: 'Grams versus servings: choose a food logging unit you can explain',
    metaTitle: 'Grams vs Servings in a Food Diary',
    description: 'Translate grams, label servings and package counts into a clear food diary entry without assuming a serving matches your portion.',
    category: 'NUTRITION TRACKING', readTime: '5 min read',
    intro: 'A serving can mean a manufacturer’s stated quantity, one item or a portion someone estimated. For people switching between kitchen scales and package labels, learning what the unit represents prevents a familiar food from producing surprisingly different diary totals.',
    takeaway: 'A label serving is a calculation basis. Your portion is what you ate. Connect them with a measured quantity or a clearly defined item count.',
    sections: [
      { id: 'define-serving', title: 'Find the quantity behind the word serving', paragraphs: [
        'Australian nutrition panels commonly show values per serving and per 100 grams or 100 millilitres. FSANZ describes these as average quantities. Start by locating the serving size and servings per package. Do not treat a front-of-pack image, a bowl icon or the word portion in a database as a measurement on its own.',
        'For a cereal labelled with a 40-gram serving, one serving means 40 grams for that panel. Your usual bowl could contain more or less. The label quantity is useful for calculation, but it does not tell you the amount you personally should eat. The diary should describe your actual bowl.',
      ] },
      { id: 'scale-example', title: 'Scale the label to a different portion', paragraphs: [
        'Imagine a product lists 160 kcal and 6 grams of protein per 40-gram serving. You measure 70 grams. Divide 70 by 40 to get 1.75 servings, then multiply each listed value by 1.75. The result is 280 kcal and 10.5 grams of protein. These values are invented solely to show the calculation.',
        'The per-100-gram route reaches the same result if the two label columns are consistent apart from rounding. Multiply each per-100-gram value by 0.7. Select the route that is easiest to check. Do not enter 70 as the number of servings when your intended quantity was 70 grams.',
      ] },
      { id: 'item-counts', title: 'Use item counts only when the item is defined', paragraphs: [
        'For individually wrapped foods, an item count can be quicker than weighing. First establish whether the nutrition panel describes one item, two items or the whole multipack. A pack with six crackers and a serving of three crackers means eating the pack represents two label servings, not six servings.',
        'Count-based entries become less reliable when individual pieces vary or a package changes size. If your usual bread changes from a smaller loaf to thick slices, review the entry instead of assuming one slice still means the same quantity. Grams are useful when item sizes are unclear, while a verified unit can keep routine logging simple.',
      ] },
      { id: 'unit-check', title: 'Check units before saving or copying a meal', paragraphs: [
        'Read your entry as a complete sentence: I ate this quantity of this food, measured using this unit. If that sentence sounds wrong, revisit the quantity before comparing the total with another day. For drinks, preserve the millilitre basis unless you have a justified weight conversion; a gram and a millilitre are not automatically interchangeable.',
        'Nexal offers free manual calorie and macro logging and copying of recent meals on Android. Calculate using the package basis, then record the portion through the supported workflow. When reusing it, check the unit along with the number. A correct quantity attached to the wrong serving definition remains an incorrect record.',
      ] },
    ],
    feature: { href: '/calorie-macro-tracker', label: 'See Nexal’s calorie and macro tracker', text: 'Build clear meal records with free manual tracking.' },
    sources: [{ href: 'https://www.foodstandards.gov.au/consumer/labelling/panels', label: 'FSANZ: nutrition information panels' }],
    faqs: [
      { question: 'Are grams always better than servings?', answer: 'No. A verified serving or item count can be convenient. Grams help when your portion differs from the defined serving or item sizes vary.' },
      { question: 'Is the serving on a package a personal recommendation?', answer: 'Use it as the basis for the label’s nutrition calculation, rather than assuming it prescribes your personal portion.' },
    ],
  },
  {
    ...publication,
    slug: 'kcal-versus-kj-food-diary',
    title: 'Kcal versus kJ: translate food energy before you log it',
    metaTitle: 'Kcal vs kJ: Food Diary Conversion Guide',
    description: 'Understand food calories and kilojoules, convert a label value and avoid mixing energy units or scaling a portion twice.',
    category: 'NUTRITION TRACKING', readTime: '5 min read',
    intro: 'An Australian package may list kilojoules while a food diary asks for calories. For shoppers using labels from different markets, that mismatch can make an ordinary snack look much larger or smaller in the log than it really is.',
    takeaway: 'Convert the energy unit and scale the portion as separate steps. Keep protein, carbohydrate and fat in their stated units rather than applying an energy conversion to them.',
    sections: [
      { id: 'energy-units', title: 'Identify which kind of number you are reading', paragraphs: [
        'Kilojoules, written kJ, and kilocalories, written kcal, both express food energy. Food calories usually refer to kilocalories in this context. FSANZ explains the approximate relationship as one kilocalorie to 4.2 kilojoules. For everyday label conversion, divide kilojoules by 4.2 to estimate kilocalories, or multiply kilocalories by 4.2 to estimate kilojoules.',
        'Check the heading before copying any number. A label showing 840 kJ is not describing 840 kcal. If both units are printed, use the one your diary requires directly. That avoids an unnecessary calculation and keeps you aligned with the manufacturer’s stated, rounded value for that food.',
      ] },
      { id: 'worked-conversion', title: 'Convert energy, then account for your portion', paragraphs: [
        'Suppose an invented snack label gives 840 kJ per 50-gram serving and you eat 75 grams. First convert 840 divided by 4.2, giving approximately 200 kcal per label serving. Then calculate 75 divided by 50, or 1.5 servings. Your portion is approximately 300 kcal. The example is arithmetic, not a suggested snack size.',
        'You can reverse the order: 840 multiplied by 1.5 gives 1,260 kJ for your portion, which becomes approximately 300 kcal. Either sequence works. The important control is performing each step once. Converting a value already shown in kcal or applying the serving multiplier again will distort the final entry.',
      ] },
      { id: 'macro-units', title: 'Keep macro grams outside the energy conversion', paragraphs: [
        'If that same label states protein in grams, leave the unit as grams. Scale it only for the portion. An invented 8 grams of protein per label serving becomes 12 grams for 1.5 servings; dividing it by 4.2 would have no meaning. Carbohydrate and fat quantities need the same separation between nutrient quantity and energy.',
        'Avoid rebuilding the label’s energy figure from macros just because the numbers do not appear perfectly aligned. The inputs may be rounded, and a simplified macro calculation does not reproduce every labelling calculation. For a straightforward packaged-food entry, preserve the label values and focus on selecting the correct column and portion.',
      ] },
      { id: 'logging-check', title: 'Use a short audit when a daily total looks unusual', paragraphs: [
        'If a familiar meal suddenly dominates your day, inspect the individual food before changing anything about your eating. Look for a kJ figure entered as kcal, a per-100-gram value treated as one serving, or a whole-package value applied to each item. These are recording problems that a dietary change would not fix.',
        'Use a calculator outside Nexal when conversion is needed, then enter the result through its free manual meal tracking on Android. This guide does not assume a built-in unit conversion setting. Keep the original unit available while checking your work, and review copied meals if the package or label source changes later.',
      ] },
    ],
    feature: { href: '/calorie-macro-tracker', label: 'Explore food energy and macro logging', text: 'Record your calculated portion with Nexal’s free core meal tools.' },
    sources: [{ href: 'https://www.foodstandards.gov.au/business/labelling/nutrition-panel-calculator/nutrients-in-the-NPC', label: 'FSANZ: energy units and nutrition calculations' }],
    faqs: [
      { question: 'How do I approximately convert kJ to kcal?', answer: 'Divide the kilojoule value by 4.2 for an everyday approximation, then scale it to the amount eaten if needed.' },
      { question: 'Should I divide protein grams by 4.2 too?', answer: 'No. That conversion is for energy. Scale protein grams for your portion without changing their unit.' },
    ],
  },
  {
    ...publication,
    slug: 'logging-restaurant-meal-estimates',
    title: 'How to log restaurant meals when the nutrition is an estimate',
    metaTitle: 'Logging Restaurant Meals as Estimates',
    description: 'Use restaurant nutrition information carefully, estimate unknown components and keep dining-out diary entries honest about uncertainty.',
    category: 'NUTRITION TRACKING', readTime: '5 min read',
    intro: 'Dinner out gives you a menu description rather than a kitchen scale. For people who want a useful diary without turning a social meal into an investigation, a clear estimate is often the most practical record available.',
    takeaway: 'Use information from the restaurant when available, check what it includes, and estimate the meal you ate without treating uncertain numbers as laboratory measurements.',
    sections: [
      { id: 'restaurant-source', title: 'Start with the restaurant’s own information', paragraphs: [
        'Look at the restaurant’s official menu or nutrition page for the exact item, size and market. A similarly named dish from another business is a weaker match. Published information can describe a standard preparation, so check whether the listed figure includes dressing, sides, a drink or the default bread before using it.',
        'Some restaurants provide more nutrition detail than others. The FDA’s menu guidance explains disclosures at covered establishments in the United States; it does not mean every restaurant worldwide has a complete macro panel. If you find calories alone, do not invent a precise protein, carbohydrate and fat split and present it as the restaurant’s data.',
      ] },
      { id: 'components', title: 'Break an unknown meal into recognisable components', paragraphs: [
        'For a cafe sandwich without published nutrition, identify the bread, filling, spread and any side you actually ate. Estimate those components using suitably described food sources. Choose plausible portions and retain the uncertainty. An elaborate calculation with guessed ingredient weights is still an estimate, even when the calculator returns several decimal places.',
        'For example, a chicken sandwich served with chips has at least two separate portion questions. Eating the whole sandwich and half the chips is different from eating half of everything. Record those shares separately where practical. If the preparation is unclear, choose a broad comparable item rather than reconstructing a secret recipe from appearance.',
      ] },
      { id: 'customisations', title: 'Account for changes without double-counting', paragraphs: [
        'Suppose a restaurant’s standard bowl includes rice and sauce, but you ask for extra rice and sauce on the side. Start with the standard dish only if you can reasonably account for those changes. Adding a full sauce serving on top of a base entry that already contains it counts the same component twice.',
        'Drinks, shared starters and finishing bites are easy to lose when you log only the named main course. Briefly review the order and what you actually consumed. There is no need to calculate another diner’s plate. For shared food, estimate your share rather than dividing equally by the number of people when the sharing was uneven.',
      ] },
      { id: 'close-entry', title: 'Save a useful estimate and move on', paragraphs: [
        'Log the best supported result once, while the meal is still easy to remember. If you keep context in a personal note, distinguish restaurant-provided values from estimated components. Later, the record will tell you what happened without encouraging you to compare an uncertain cafe meal to a carefully measured home meal as though both were equally precise.',
        'Nexal’s free manual meal, calorie and macro logging on Android can hold your calculated entry. Premium AI estimates are optional and cannot establish an unseen recipe. Review one-off meals within the wider diary, rather than changing future portions to compensate for a number whose accuracy you cannot verify. Better records begin with honest assumptions.',
      ] },
    ],
    feature: { href: '/calorie-macro-tracker', label: 'Log meals manually in Nexal', text: 'Keep dining-out estimates alongside your everyday food records.' },
    sources: [{ href: 'https://www.fda.gov/regulatory-information/search-fda-guidance-documents/guidance-industry-labeling-guide-restaurants-and-retail-establishments-selling-away-home-foods-part-0', label: 'FDA: restaurant menu nutrition guidance, United States' }],
    faqs: [
      { question: 'What if the restaurant only publishes calories?', answer: 'Use that figure for the matching item and portion. Any macro breakdown from another source remains a separate estimate, not official restaurant information.' },
      { question: 'Should I log an uncertain restaurant meal at all?', answer: 'If tracking is useful to you, an acknowledged estimate can preserve context. It does not need to be represented as exact.' },
    ],
  },
  {
    ...publication,
    slug: 'packed-lunch-food-diary',
    title: 'A packed lunch food diary that follows what you actually eat',
    metaTitle: 'Packed Lunch Food Diary: A Practical Routine',
    description: 'Organise lunch components, keep packaging details and reconcile packed food with what you actually eat at work or on the go.',
    category: 'NUTRITION TRACKING', readTime: '5 min read',
    intro: 'Packing lunch gives you a chance to capture quantities before leaving home, but the lunchbox is not proof of what you ate. This routine is for commuters and shift workers whose packed meals sometimes return half-full or gain a last-minute cafe extra.',
    takeaway: 'Prepare the information at packing time, confirm consumption after lunch, and keep snacks and drinks separate enough to update when the day changes.',
    sections: [
      { id: 'packing-time', title: 'Capture details where the labels and scale are', paragraphs: [
        'When assembling lunch, identify the main meal, separate snacks and drinks. Keep the label information for unfamiliar products or calculate the portion before throwing packaging away. You do not need an elaborate spreadsheet. A short personal note with the food name, quantity and calculation source can prevent an uncertain reconstruction during a busy break.',
        'For a sandwich, establish the bread quantity, filling and spread separately before combining their nutrition into a meal entry. If lunch is leftovers, use the calculation for that particular dish and portion. The goal is to make information available later, rather than logging the entire lunchbox as consumed the moment you zip the bag.',
      ] },
      { id: 'separate-components', title: 'Separate components that might be eaten at different times', paragraphs: [
        'Imagine you pack a wrap, a yoghurt and a piece of fruit. At lunch you eat the wrap and half the yoghurt, then save the fruit for the trip home. Treating all three as one fixed meal makes it harder to describe that day. Keep enough component detail to record the yoghurt share and fruit when consumed.',
        'If the wrap contains several ingredients but is always eaten as a whole, combining its calculated nutrition can be convenient. That is different from combining the wrap with every optional snack. Choose your logging boundary according to what might change independently, not simply which foods were stored in the same container.',
      ] },
      { id: 'after-lunch', title: 'Reconcile packed, eaten and returned food', paragraphs: [
        'After your break, check whether anything came back, was shared or was replaced. A purchased coffee, an extra roll or a colleague’s biscuit belongs to the day if you consumed it. Add those items without assuming that lunch must still match the plan you made at home. Equally, do not leave a full portion logged when some remains uneaten.',
        'A simple worked distinction is two packed wraps with one eaten at noon and one taken home. The noon record is one wrap, not the entire batch. If the second wrap is later eaten, record it then. Preparing food and consuming food are separate events, even when the original quantity is easy to calculate.',
      ] },
      { id: 'repeat-workdays', title: 'Reuse the routine while checking each workday', paragraphs: [
        'Nexal supports free manual meal logging and copying recent meals on Android. A recurring lunch can start from a recent entry, provided you check the ingredients, quantity and actual consumption. Reusing yesterday’s wrap may save effort; it should not also bring back yesterday’s yoghurt when today you packed something else.',
        'At the end of a few workdays, look for information gaps rather than judging the menu. Perhaps you consistently forget the drink or cannot remember the afternoon snack. Put the relevant detail within reach at packing time. The best lunch diary routine fits the real break you have, including interruptions and changing plans, without requiring perfect recall.',
      ] },
    ],
    feature: { href: '/calorie-macro-tracker', label: 'Explore everyday meal logging', text: 'Try free manual tracking and recent-meal copying in Nexal for Android.' },
    sources: [{ href: '/calorie-macro-tracker', label: 'Nexal: free core meal and macro tracking' }],
    faqs: [
      { question: 'Should I log lunch when I pack it?', answer: 'You can prepare the calculation then, but confirm the diary reflects the amount actually eaten after plans and portions change.' },
      { question: 'Can I copy yesterday’s lunch?', answer: 'Yes, Nexal supports copying recent meals. Review the portion and components before treating the copied entry as today’s record.' },
    ],
  },
  {
    ...publication,
    slug: 'breakfast-logging-routine',
    title: 'Build a breakfast logging routine for rushed mornings',
    metaTitle: 'Breakfast Logging Routine for Busy Mornings',
    description: 'Capture breakfast quantities, drinks and changing toppings with a small routine that works before work, travel or an early shift.',
    category: 'NUTRITION TRACKING', readTime: '5 min read',
    intro: 'The first meal you eat may happen at a desk, in a kitchen or well into the morning. This guide helps people who know their usual breakfast but forget its changing details once the day gets moving. It is a logging routine, not a recommendation to eat at a particular time.',
    takeaway: 'Anchor logging to preparing or finishing your meal. Separate the familiar base from changing additions, and record drinks as carefully as the food.',
    sections: [
      { id: 'choose-anchor', title: 'Attach the record to something that already happens', paragraphs: [
        'Choose one existing moment, such as putting the bowl down or clearing the cup, as your cue to record breakfast. A routine connected to an action can be easier to remember than an intention to catch up sometime later. If you cannot enter the meal immediately, keep a quick personal quantity note while the information is visible.',
        'For early shifts, the useful anchor may be opening a packed container rather than waking up. For a cafe breakfast, it may be checking the receipt after eating. Design the cue around your actual morning. The diary needs the food and portion you consumed, regardless of whether it matches a conventional breakfast schedule.',
      ] },
      { id: 'base-and-extras', title: 'Separate the base meal from variable additions', paragraphs: [
        'A usual bowl might contain cereal and milk, while fruit, nuts or a spoon of spread varies by day. Capture the stable components once using their labels and actual quantities. Then review the changing components each morning. Calling the whole bowl usual breakfast can hide meaningful differences in ingredient amounts or products.',
        'For example, Monday’s bowl contains 45 grams of one cereal, while Tuesday’s contains 60 grams and a different milk. A copied Monday entry needs both changes reviewed. Do not assume the cereal scoop or the level of liquid looks identical. If you choose estimated quantities, keep that approach understandable instead of describing the entry as weighed.',
      ] },
      { id: 'drinks', title: 'Give the morning drink its own quick check', paragraphs: [
        'Coffee and tea orders are easy to remember by name but harder to reconstruct by ingredients. A homemade drink may change when you add milk or sugar. A cafe drink may vary with cup size, milk choice and syrup. Record the preparation you received rather than copying a generic coffee entry without reading its description.',
        'If you finish only part of the drink, estimate that share consistently. The same applies to a smoothie made for two people: the blender’s full contents are not automatically your portion. Check whether the food entry already includes the additions before adding them again. This is a completeness check, not a reason to remove foods you enjoy.',
      ] },
      { id: 'morning-reset', title: 'Recover from a missed morning without rebuilding everything', paragraphs: [
        'If breakfast slips past unlogged, reconstruct the parts you remember and acknowledge the rest as estimates. Use the current package or usual recipe as a reference, but do not overwrite today with yesterday solely to fill a gap. A simple, reasonably described record is more informative than an apparently exact entry made from uncertain memory.',
        'Nexal’s free manual meal and macro logging on Android can support this routine, and copying recent meals can shorten repeated breakfasts. After a week, identify one point of friction: perhaps toppings change often, or takeaway cup sizes are unclear. Improve that part of the routine before adding more tracking detail to every morning.',
      ] },
    ],
    feature: { href: '/calorie-macro-tracker', label: 'Try a straightforward breakfast log', text: 'Record meals free in Nexal and review quantities when copying a recent breakfast.' },
    sources: [{ href: '/calorie-macro-tracker', label: 'Nexal: manual meal, calorie and macro tracking' }],
    faqs: [
      { question: 'Do I need to eat breakfast to use a food diary?', answer: 'No. Record your actual eating routine. This article explains logging, not when you should eat.' },
      { question: 'Can I reuse a standard breakfast entry?', answer: 'Yes, if you check today’s products, quantities, toppings and drink. Repetition is convenient when the entry still matches the meal.' },
    ],
  },
  {
    ...publication,
    slug: 'vegetarian-meal-planning-app-selection',
    title: 'Choosing a vegetarian meal planning app for your real kitchen',
    metaTitle: 'Choose a Vegetarian Meal Planning App',
    description: 'Evaluate vegetarian meal planning apps with a practical meal brief, ingredient checks and a clear distinction between suggestions and dietary advice.',
    category: 'MEAL PLANNING', readTime: '5 min read',
    intro: 'Vegetarian describes a starting preference, not a complete cooking brief. For someone choosing an app, the useful question is whether its suggestions fit the foods you eat, equipment you own and time you have. A vegetarian label alone cannot answer those questions.',
    takeaway: 'Test one realistic meal before evaluating a whole week. Check ingredients, preparation and portions yourself, and keep clinical dietary needs outside an app-selection exercise.',
    sections: [
      { id: 'preference-brief', title: 'Describe what vegetarian means in your household', paragraphs: [
        'Write a short brief before trying a planner: whether you eat eggs and dairy, ingredients you avoid, meals you already enjoy and who is cooking. Distinguish a preference from an allergy or a medical restriction. A generated vegetarian suggestion should never be treated as confirmation that a meal is safe for a particular allergy.',
        'The NHS vegetarian diet page discusses a range of foods, including pulses, eggs and alternatives such as tofu. That variety is useful context for reviewing whether an app keeps offering the same ingredient. It does not establish that a generated week meets your individual nutritional needs or that its macro totals demonstrate overall dietary adequacy.',
      ] },
      { id: 'test-meal', title: 'Use a demanding but realistic test meal', paragraphs: [
        'Try a brief such as a weekday vegetarian dinner for one cook, using a hob, ingredients available at the local shop and leftovers that can become tomorrow’s lunch. Ask whether the proposal actually meets those practical constraints. A recipe with six separate cooked components may be vegetarian yet still be unsuitable for your available evening.',
        'Inspect the full ingredient list rather than only the title. Stocks, sauces and toppings can matter to your preferences. Then check whether the quantities match the stated number of portions and whether the instructions use every ingredient. This small trial reveals more about usefulness than the number of recipes advertised on a feature page.',
      ] },
      { id: 'nutrition-review', title: 'Review nutrition figures without turning them into a verdict', paragraphs: [
        'If a planner supplies macros, check the serving basis and the actual products you would buy. Two dairy alternatives or meat substitutes can have different labels, so a generic ingredient estimate may need revision. A meal plan with tidy numbers can still contain a portion ambiguity or an ingredient your household does not use.',
        'Consider a proposed bean wrap where you replace the specified filling with a different packaged product. Recalculate using your selected product and amount rather than preserving the original macro line. The purpose is to make the record match your cooking. For advice about a restricted diet or individual nutritional needs, use an appropriately qualified professional.',
      ] },
      { id: 'app-fit', title: 'Match free tracking and paid planning to separate needs', paragraphs: [
        'Nexal is available on Android. Its free manual meal, calorie and macro logging can help record vegetarian meals you already prepare. Premium adds AI meal planning and macro estimates. Review any generated suggestions against your brief; this guide does not promise a dedicated vegetarian certification, allergen filter or clinical meal planning service.',
        'Choose the tool according to the work you need help with. If you already have reliable recipes, a diary may be sufficient. If deciding what to cook is the difficult part, trial a planning workflow and inspect the output. Keep a short checklist of ingredient suitability, preparation effort, portion clarity and realistic shopping before deciding it fits.',
      ] },
    ],
    feature: { href: '/ai-meal-planner', label: 'Explore Nexal Premium meal planning', text: 'Compare optional AI planning with free logging for meals you already cook.' },
    sources: [{ href: 'https://www.nhs.uk/live-well/eat-well/how-to-eat-a-balanced-diet/the-vegetarian-diet/', label: 'NHS: vegetarian food variety' }, { href: '/ai-meal-planner', label: 'Nexal: Premium AI meal planning' }],
    faqs: [
      { question: 'Does a vegetarian meal plan prove my diet meets my needs?', answer: 'No. Generated meals and macro figures are not an individual nutritional assessment. Seek qualified advice for clinical or complex dietary needs.' },
      { question: 'Do I need Premium to log vegetarian meals in Nexal?', answer: 'No. Core manual meal and macro logging are free. AI meal planning is a Premium feature.' },
    ],
  },
  {
    ...publication,
    slug: 'budget-grocery-planning-with-food-app',
    title: 'Use a food diary to make budget grocery planning more realistic',
    metaTitle: 'Budget Grocery Planning with a Food Diary',
    description: 'Turn meals you actually eat into a practical grocery plan, compare usable quantities and avoid assuming an app knows current prices.',
    category: 'MEAL PLANNING', readTime: '5 min read',
    intro: 'A meal plan can look affordable until you buy full packages for ingredients used once. This approach is for shoppers who want to connect their actual meals with a practical list and a budget, using the diary as evidence rather than a promise of savings.',
    takeaway: 'Plan from food you have, meals you finish and quantities you will use. Check current shop prices yourself and keep the shopping list separate from nutrition totals.',
    sections: [
      { id: 'inventory', title: 'Start with your kitchen and last week’s meals', paragraphs: [
        'Before choosing recipes, check the pantry, fridge and freezer. Then review several recent meals and ask which you actually prepared, which produced leftovers and which ingredients went unused. USDA MyPlate’s budget guidance supports planning and using food on hand. Your diary adds personal context about what gets eaten in your household.',
        'Suppose last week included two rice bowls and a pasta dinner, while an ambitious recipe never happened. That is a planning clue. Build the next list around meals that fit your schedule, with a realistic place for existing ingredients. Do not buy another set of specialised ingredients merely because a planner generated an attractive alternative.',
      ] },
      { id: 'usable-cost', title: 'Compare the quantity you will use, not only the sticker', paragraphs: [
        'Consider two invented yoghurt options: a 500-gram tub for $4 and a one-kilogram tub for $7. The larger tub costs less per 100 grams, but it requires more cash at checkout and only helps if you use it. If half goes unused, its nominal unit price does not describe the value you obtained.',
        'For a planned meal, distinguish recipe cost from shopping-basket cost. A recipe may use a small amount of a spice you do not own, yet buying the jar affects this week’s budget. Write both the required quantity and what you need to purchase. The arithmetic becomes useful only when it reflects your cupboard and likely use.',
      ] },
      { id: 'ingredient-overlap', title: 'Give leftover ingredients an explicit second job', paragraphs: [
        'Plan ingredient overlap deliberately. If a dish uses half a bag of vegetables, identify another meal that can use the remainder. That second meal should have its own realistic time slot, rather than relying on a vague intention to use things up. Check storage and preparation instructions appropriate to the foods you buy.',
        'A practical example is a wrap filling that also works in a rice bowl, with each meal logged for its own components and portions. The wrap and rice are different additions, so the nutrition record should change even if the filling is repeated. Ingredient reuse can simplify shopping without requiring every meal to be nutritionally identical.',
      ] },
      { id: 'app-boundaries', title: 'Use the app for records and verify the budget outside it', paragraphs: [
        'Nexal’s free Android meal logging lets you record meals and copy recent meals that recur. Premium AI planning can provide ideas, but this guide does not claim live supermarket pricing, price comparisons or automatic shopping-list generation. Keep a paper or separate digital shopping list and check current local prices before choosing substitutions.',
        'After the shop, compare intended meals with what you used. If an ingredient was repeatedly left over, change the quantity or plan next time. If a proposed substitute needs several additional purchases, reconsider the total basket. Judge the workflow by fewer planning surprises and clearer decisions, rather than assuming any app guarantees a particular saving.',
      ] },
    ],
    feature: { href: '/ai-meal-planner', label: 'See optional meal planning in Nexal', text: 'Use free meal records as a starting point, with Premium planning when ideas are the difficult part.' },
    sources: [{ href: 'https://www.myplate.gov/web/eat-healthy/healthy-eating-budget', label: 'USDA MyPlate: planning food within a budget' }, { href: '/ai-meal-planner', label: 'Nexal: AI meal planning features' }],
    faqs: [
      { question: 'Does Nexal compare supermarket prices?', answer: 'This guide does not claim supermarket pricing tools. Check local prices yourself and keep your budget calculation alongside the meal plan.' },
      { question: 'Is a bigger pack always better value?', answer: 'Compare unit price, available budget and the amount you will actually use. A lower unit price alone does not settle the purchase.' },
    ],
  },
  {
    ...publication,
    slug: 'family-meal-portions-tracking',
    title: 'Track your portion of a family meal without dividing everything equally',
    metaTitle: 'Family Meal Portions: Track Your Own Plate',
    description: 'Record your share of a shared dinner, separate uneven components and account for second helpings without assigning everyone the same portion.',
    category: 'NUTRITION TRACKING', readTime: '5 min read',
    intro: 'A dinner cooked for four people does not necessarily produce four equal plates. This guide is for adults recording their own intake when meals are shared, served in components or followed by second helpings. It is not a system for setting targets for other family members.',
    takeaway: 'Track your plate and any later additions. Use a batch fraction only when it represents what you served, and separate components when the distribution is uneven.',
    sections: [
      { id: 'personal-share', title: 'Keep the household meal and your intake separate', paragraphs: [
        'Begin with what is known about the dinner: the dish, ingredients and finished quantities where practical. Then identify the portion you actually ate. The number of people at the table is not enough to establish your fraction. Someone may take a smaller plate, leave a component or eat something different.',
        'For a personal diary, there is no need to create food targets for everyone else. You can record your own serving while leaving other diners’ choices alone. If you are not the cook, ask about the main ingredients and relevant additions rather than pretending to know an exact recipe from its name or appearance.',
      ] },
      { id: 'mixed-dish', title: 'Use a batch fraction for a reasonably uniform dish', paragraphs: [
        'Suppose a well-mixed lentil dish has an estimated batch total of 1,800 kcal and a finished edible weight of 1,500 grams. Your 300-gram portion represents one fifth of the batch, or approximately 360 kcal. The figures are invented for arithmetic. Apply the same fraction to the calculated macro totals if the ingredients are distributed reasonably evenly.',
        'Now imagine you add another 100 grams. That is an additional one fifteenth of the batch, or approximately 120 kcal in this example. Record the extra serving rather than leaving the first plate as the entire dinner. The method describes consumed portions; it does not recommend how large either serving should be.',
      ] },
      { id: 'separate-components', title: 'Switch methods when the meal is not evenly mixed', paragraphs: [
        'A roast dinner or build-your-own taco spread is not one uniform food. Two plates of equal weight can contain different shares of meat, vegetables, tortillas and sauce. Calculate the components on your plate separately when you can. A general whole-dinner fraction can create false precision if you selected a different mix from the rest of the table.',
        'Keep additions visible: gravy, salad dressing and toppings may be shared but still vary by plate. If you only know an estimated spoonful, use an appropriate approximate entry. Do not count the entire sauce jug as your intake, and do not add a full sauce serving separately if your chosen prepared-meal entry already includes it.',
      ] },
      { id: 'family-routine', title: 'Build a low-friction dinner check', paragraphs: [
        'Choose a brief moment after dinner to review the initial serving, second helpings and anything left on your plate. If weighing at the table is impractical, use clearly described estimates. Consistency in your method helps you interpret the record, but it does not transform estimated shared portions into exact nutrition measurements.',
        'Nexal provides free manual meal and macro logging on Android. Calculate your portion externally if needed and enter its nutrition through the supported tools. When copying a familiar family dinner, update your plate rather than assuming last week’s portion. This approach keeps the diary personal and useful without turning a shared meal into a household tracking project.',
      ] },
    ],
    feature: { href: '/calorie-macro-tracker', label: 'Record your own dinner portion', text: 'Use Nexal’s free manual logging for shared meals and individual servings.' },
    sources: [{ href: '/calorie-macro-tracker', label: 'Nexal: meal and macro logging on Android' }],
    faqs: [
      { question: 'Can I divide a family meal by the number of diners?', answer: 'Only when that reasonably reflects the portions eaten. Unequal servings and different component choices need a more specific estimate.' },
      { question: 'How do I handle second helpings?', answer: 'Include the additional quantity in your personal record. Calculate it separately or update the total consumed portion, avoiding duplicate entries.' },
    ],
  },
  {
    ...publication,
    slug: 'food-barcode-scanner-limitations',
    title: 'Food barcode scanner limitations: what a successful scan does not verify',
    metaTitle: 'Food Barcode Scanner Limits and Checks',
    description: 'Check a scanned food’s identity, nutrition basis and portion, and understand why barcode lookup is not an ingredient or allergy guarantee.',
    category: 'NUTRITION TRACKING', readTime: '5 min read',
    intro: 'A barcode scan can shorten the search for a packaged food, but a recognised code is only the beginning of a useful entry. This guide is for shoppers deciding when scanning helps and what still needs checking before the food reaches their diary.',
    takeaway: 'Verify the returned product against the package, then enter your actual quantity. A scanner identifies a lookup candidate; it does not measure what you ate.',
    sections: [
      { id: 'identifier', title: 'Understand what the barcode contributes', paragraphs: [
        'GS1 explains that common retail barcodes encode identifiers such as the Global Trade Item Number. In a food logging workflow, that identifier can help retrieve a product record. It is not a direct measurement of the calories, protein or portion in front of you. Those details depend on the product information returned by the lookup.',
        'Read the result before accepting it. Check the product name, brand, flavour and package description. A familiar logo alone is not enough if the item is a different variety. Successful recognition of the code does not establish that every displayed field matches the package you currently hold or the amount you intend to eat.',
      ] },
      { id: 'nutrition-basis', title: 'Compare the nutrition basis with the physical label', paragraphs: [
        'Look at the returned serving size and compare it with the package. A record can appear plausible while using a different portion basis. An entry showing 200 kcal might describe one serving, 100 grams or the whole package. Establish which interpretation applies before entering a count or quantity.',
        'For example, an invented snack pack contains two 30-gram pieces, while the displayed entry describes 30 grams. Eating both pieces means twice that entry’s nutrition, assuming it matches the label. Entering one simply because you ate one package would understate the portion. Scanning saves searching effort; it does not resolve the relationship between packages and servings.',
      ] },
      { id: 'missing-results', title: 'Have a manual fallback for missing or questionable results', paragraphs: [
        'A missing result does not make the food impossible to log. Use its nutrition panel and the quantity consumed to calculate the entry manually. If the result conflicts with the package, use the current label rather than selecting whichever number seems more convenient. Keep energy units and serving units clear during that calculation.',
        'Do not treat barcode lookup as allergy verification. NSW Food Authority advises checking labels each time a product is purchased because ingredients can change. Use the physical ingredient and allergen information and appropriate advice when safety matters. A nutrition diary record and an allergen assessment answer different questions, even when they refer to the same product.',
      ] },
      { id: 'nexal-choice', title: 'Decide whether scanning solves your logging problem', paragraphs: [
        'Nexal offers barcode scanning with Premium on Android. Core manual meal, calorie and macro logging are free. If most of your meals are homemade or you already have clear label calculations, manual entry may be enough. Scanning is most relevant when locating packaged-food information is a recurring source of effort for you.',
        'Before relying on any scanned entry, ask four questions: is this the right product, is the nutrition basis clear, does it match the label, and is the quantity what I ate? No specific database coverage or accuracy rate is promised here. A fast entry is useful when these basic checks survive the shortcut.',
      ] },
    ],
    feature: { href: '/calorie-macro-tracker', label: 'Compare Nexal’s nutrition logging options', text: 'Start with free manual tracking; barcode scanning is an optional Premium tool.' },
    sources: [{ href: 'https://www.gs1.org/standards/barcodes', label: 'GS1: barcodes and product identifiers' }, { href: 'https://www.foodauthority.nsw.gov.au/consumer/food-allergies/living-food-allergy/reading-food-labels', label: 'NSW Food Authority: checking current food labels' }],
    faqs: [
      { question: 'Does a barcode contain my portion’s macros?', answer: 'A common retail barcode identifies the product for lookup. You still need to check the returned information and specify your portion.' },
      { question: 'Is barcode scanning free in Nexal?', answer: 'Barcode scanning is a Premium feature. Core manual meal, calorie and macro logging remain free.' },
    ],
  },
  {
    ...publication,
    slug: 'reviewing-ai-photo-macro-estimates',
    title: 'Reviewing AI photo macro estimates before adding them to a diary',
    metaTitle: 'Review AI Photo Macro Estimates Before Logging',
    description: 'Audit a photo-based food estimate for identity, portion, hidden ingredients and double-counting before using it in your meal record.',
    category: 'NUTRITION TRACKING', readTime: '5 min read',
    intro: 'A meal photo can help you remember what was on the plate, but an estimate generated from it still needs review. This guide is for anyone assessing photo-based macro output from an AI tool. It does not establish that Nexal offers photo scanning.',
    takeaway: 'Treat photo output as a proposed food description and calculation. Verify the foods, amounts and unseen additions using information you actually have before logging the result.',
    sections: [
      { id: 'identity-audit', title: 'First check whether the output describes the right food', paragraphs: [
        'Read the proposed meal in plain language before looking at the total. Does it identify the filling, grain and preparation correctly? A wrap can contain ingredients hidden by the bread, and a bowl can hide layers beneath its topping. If you know the recipe or have the package, that information is a better basis for those details than appearance alone.',
        'Imagine an estimate describes a bowl as chicken, rice and yoghurt sauce, but you know it contains tofu and a different dressing. Correct the ingredient assumptions before considering the macro figure. A believable total attached to the wrong dish is not made useful by its precision. Food identity comes before portion arithmetic.',
      ] },
      { id: 'portion-audit', title: 'Check quantity and consumption separately', paragraphs: [
        'A photo of a plate does not tell a reviewer how much you ultimately ate. Start with any quantity information you have: the package serving, a measured ingredient amount or a known share of a batch. Then account for leftovers. If you ate half the pictured meal, the original whole-plate estimate needs review before entering it.',
        'Suppose the tool assumes a rice component weighs 250 grams, while your kitchen record says 150 grams cooked. Replace that assumption using a compatible cooked-food source. Do not reduce the entire bowl proportionally if only the rice amount was wrong. The other components may have been estimated on different assumptions and need their own checks.',
      ] },
      { id: 'hidden-ingredients', title: 'Ask what the image cannot settle', paragraphs: [
        'Review oil, sauces, spreads, drinks and ingredients mixed into the dish. You might know these from cooking, a menu description or the person who prepared the meal. If you do not, acknowledge the uncertainty instead of assuming either that the estimate included everything or that an extra full sauce portion should always be added.',
        'Double-counting is a particular risk when you combine a whole-dish estimate with separate ingredients. If the output already represents dressed pasta, adding the full dressing again may repeat it. Choose whether your final record uses a reviewed combined estimate or component calculations. Keep that choice clear enough to avoid counting the same food through two methods.',
      ] },
      { id: 'final-review', title: 'Use the estimate only at the confidence it deserves', paragraphs: [
        'Ask what the result would change in your diary. If a large part of the recipe is unknown, a broad estimate may still preserve context, but it should not drive a precise conclusion about that day. You can keep a personal note of uncertain components without claiming the AI tool supplied a reliable confidence score.',
        'Nexal Premium includes AI macro estimates; free manual meal and macro logging is available on Android. Photo analysis is not a verified Nexal feature in this guide. You can apply this review process to output from another tool, then manually record a supported calculation. Pay for assistance only if reviewing it still leaves you with a useful workflow.',
      ] },
    ],
    feature: { href: '/calorie-macro-tracker', label: 'Explore Nexal’s meal logging workflow', text: 'Keep reviewed estimates in a free manual food diary; Premium macro estimates are optional.' },
    sources: [{ href: '/calorie-macro-tracker', label: 'Nexal: tracking and the limits of nutrition estimates' }],
    faqs: [
      { question: 'Does this article mean Nexal has a food photo scanner?', answer: 'No. Photo scanning is not established here. Nexal’s verified offer includes Premium AI macro estimates and free manual logging.' },
      { question: 'Can a photo estimate replace known ingredient quantities?', answer: 'Use known recipe, package and measured quantity information to review the estimate. A photo-based guess should not override better information you have.' },
    ],
  },
  {
    ...publication,
    slug: 'food-diary-versus-macro-planner',
    title: 'Food diary versus macro planner: record the day or organise the next one',
    metaTitle: 'Food Diary vs Macro Planner: Choose Your Tool',
    description: 'Distinguish meal records from planning suggestions, choose the workflow you need and reconcile a planned menu with what you actually ate.',
    category: 'MEAL PLANNING', readTime: '5 min read',
    intro: 'If you know what to cook but forget what you ate, you have a different problem from someone staring at an empty weekly menu. This comparison is for people choosing between a food diary and a macro planner based on the work they actually need help doing.',
    takeaway: 'Use the diary to describe consumption and the planner to propose meals. Keep both useful by reconciling changes rather than treating a planned menu as a completed food record.',
    sections: [
      { id: 'diary-purpose', title: 'A food diary answers what happened', paragraphs: [
        'A diary records food, quantities and calculated nutrition for the meals you consumed. Its usefulness depends on the entries matching the day. It can help you see recurring logging gaps or remember a meal you want to repeat, even when you are not using a numerical target. You already know the meal; the task is recording it clearly.',
        'For example, someone who cooks familiar dinners but forgets lunch drinks needs an easier capture routine, not necessarily a new menu. Try recording one representative day and finding it again. If that solves the immediate problem, adding planning complexity may not improve the basic habit that was missing.',
      ] },
      { id: 'planner-purpose', title: 'A macro planner answers what you might prepare', paragraphs: [
        'A planner proposes meals around supplied preferences and targets. It can reduce the blank-page task of deciding what to cook, but its output is still an intention. Ingredient availability, portion changes and substitutions can alter the food you eventually eat. A generated set of macros does not prove a target is appropriate for you.',
        'Consider a plan for a rice bowl with a particular filling. At the shop you choose another product, and at dinner you serve a different quantity of rice. The planner has still provided a useful idea, but its original nutrition line no longer describes the plate. The diary needs the revised ingredients and actual portion.',
      ] },
      { id: 'workflow-decision', title: 'Choose a starting workflow using one concrete problem', paragraphs: [
        'Write down the point where your routine stalls. If it is remembering quantities, start with logging and a simple portion method. If it is choosing meals, test planning with a realistic cooking brief. If it is understanding nutrition for a particular health condition, a general diary or planner is not a substitute for qualified individual guidance.',
        'You may need both planning and recording, but test them separately. For a busy week, select two familiar dinners and one suggested meal. Record what actually happens. This small comparison can show whether suggestions reduce useful work or create a second menu to manage, without committing you to a complicated full-week process.',
      ] },
      { id: 'nexal-workflow', title: 'Keep planned meals and actual entries in agreement', paragraphs: [
        'Nexal’s core manual meal, calorie and macro logging is free on Android. Premium adds AI meal planning and AI macro estimates. Start with the free diary if you already have meals to record. Consider Premium when organising meal ideas is the task you want help with, then review the proposed ingredients and portions.',
        'After eating, compare the proposal with the meal consumed. Update substitutions and quantities through the supported logging workflow, and avoid leaving both a planned-meal estimate and a second component entry for the same food. This guide does not assume automatic reconciliation. A brief manual check keeps the historical record useful when the day departs from the menu.',
      ] },
    ],
    feature: { href: '/ai-meal-planner', label: 'Compare Nexal meal planning and logging', text: 'Try free tracking first and add Premium planning if meal ideas are your main obstacle.' },
    sources: [{ href: '/ai-meal-planner', label: 'Nexal: AI meal planning and free tracking' }],
    faqs: [
      { question: 'Can I use a food diary without following a meal plan?', answer: 'Yes. Record what you eat using suitable food information and portions. A separate planned menu is optional.' },
      { question: 'Does following a plan remove the need to check my log?', answer: 'No. Substitutions, servings and uneaten food can make actual intake differ from the original plan.' },
    ],
  },
  {
    ...publication,
    slug: 'logging-sauces-oils-and-additions',
    title: 'Logging sauces, oils and additions without counting them twice',
    metaTitle: 'Log Sauces, Oils and Meal Additions Clearly',
    description: 'Track spreads, dressings and cooking additions with practical portion examples, shared-recipe checks and a clear rule against double-counting.',
    category: 'NUTRITION TRACKING', readTime: '5 min read',
    intro: 'The main food is easy to remember; the spread, dressing or oil often disappears from the record. This guide is for home cooks whose diary describes the centre of the plate but leaves out additions or counts them again inside a prepared-food entry.',
    takeaway: 'Identify which additions are already included, calculate the amount associated with your portion, and keep uncertainty visible when some remains in a pan or serving dish.',
    sections: [
      { id: 'entry-boundary', title: 'Decide what the main entry already includes', paragraphs: [
        'Read the food description before adding extras. Plain cooked vegetables and vegetables roasted with oil are different entry assumptions. A generic sandwich may already include a spread. If you cannot tell what a prepared-meal entry contains, use a clearer source or calculate components instead of adding every possible topping to an ambiguous total.',
        'A useful rule is one calculation for each consumed component. You can use a combined meal estimate or separate ingredients, provided their boundaries are clear. The problem is overlap: logging a full dressed salad and then all its dressing again. Missing additions and repeated additions can both make a familiar meal difficult to interpret.',
      ] },
      { id: 'label-example', title: 'Calculate a dressing portion from its label', paragraphs: [
        'Imagine a dressing label states 120 kcal and 10 grams of fat per 30-gram serving. You use 18 grams on your own salad. The portion multiplier is 18 divided by 30, or 0.6. The calculated addition is 72 kcal and 6 grams of fat. These are invented label values, not the nutrition of a named product.',
        'Apply the same multiplier to the other stated nutrients. If your measure is a spoonful, establish whether the label defines that spoonful by weight or volume. Do not assume a heaped spoon is identical to the manufacturer’s measure. When weighing is impractical, use an acknowledged estimate rather than disguising an approximate spoon as an exact gram quantity.',
      ] },
      { id: 'shared-cooking', title: 'Treat shared cooking oil as part of a recipe estimate', paragraphs: [
        'If oil goes into a shared dish, include it in the ingredient calculation and assign your share using a reasonable portion method. That is different from logging the entire amount poured as your personal serving. If substantial oil or sauce remains behind, the amount added does not necessarily equal the amount consumed.',
        'You often cannot measure the exact retained amount in everyday cooking. Record the assumption that best fits the available information rather than claiming a precise absorption percentage. Do not apply an invented retention rule to every fried or roasted food. For a prepared-food entry that already accounts for cooking fat, check before adding another oil estimate.',
      ] },
      { id: 'final-pass', title: 'Give additions one brief review at the end of the meal', paragraphs: [
        'Scan the preparation and plate in order: cooking additions, spreads, dressings, toppings and anything served separately. Include what belongs to your consumed portion and remove any overlap with the main entry. This is a practical completeness check, not an instruction to eliminate sauces or make every meal plain for easier tracking.',
        'Nexal offers free manual meal and macro logging on Android. You can calculate an addition externally and incorporate it into your meal record. If you copy a recent meal, check today’s dressing and toppings rather than inheriting yesterday’s choices. A small variation can be recorded directly without replacing the entire familiar meal or treating every addition as a new recipe.',
      ] },
    ],
    feature: { href: '/calorie-macro-tracker', label: 'Build a more complete meal record', text: 'Use free manual logging for meals and the additions you actually consume.' },
    sources: [{ href: '/calorie-macro-tracker', label: 'Nexal: manual nutrition logging and estimates' }],
    faqs: [
      { question: 'Should I always add cooking oil separately?', answer: 'Check whether it is already represented in the recipe or prepared-food entry. Count the consumed food once using a clear method.' },
      { question: 'What if I do not know how much sauce I ate?', answer: 'Use the best reasonable portion estimate and keep its uncertainty in mind. Do not present a guessed amount as a measured one.' },
    ],
  },
  {
    ...publication,
    slug: 'checking-duplicate-food-entries',
    title: 'Checking duplicate food entries when the same name gives different macros',
    metaTitle: 'Check Duplicate Food Entries Before Logging',
    description: 'Compare similar food records by product identity, preparation and serving basis, then check your diary for accidental repeated meals.',
    category: 'NUTRITION TRACKING', readTime: '5 min read',
    intro: 'Two entries both say yoghurt, yet their totals disagree. For anyone using food databases or comparing saved calculations, the name alone is not enough to choose a record. Start by separating legitimate differences from entries that describe the same food inconsistently.',
    takeaway: 'Normalise the comparison before choosing an entry. Check product, preparation, quantity and source, then make sure the selected food appears only as often as you consumed it.',
    sections: [
      { id: 'same-food', title: 'Establish whether the entries describe the same thing', paragraphs: [
        'Compare the full descriptions: brand, flavour, fat level, preparation and edible form. Plain yoghurt and a sweetened variety can share a short search name without being equivalent. Rice described as dry and rice described as cooked also represent different measurement states. A differing total is not automatically evidence that one record is wrong.',
        'For a packaged product, start with the label you have. Check whether each candidate refers to that exact variety and package context. For generic food, prefer a source with a clear preparation description. Resist choosing the smallest or most convenient number; the aim is a match to the food, rather than a preferred daily total.',
      ] },
      { id: 'normalise-units', title: 'Compare both records on the same quantity basis', paragraphs: [
        'Suppose an invented entry lists 90 kcal per 100 grams while another lists 135 kcal per 150-gram pot. Dividing 135 by 1.5 gives 90 kcal per 100 grams, so the energy figures agree. What looked like a conflict was a difference in serving basis. Repeat the comparison for macros if you need to verify those fields.',
        'A portion called one bowl cannot be compared usefully until its quantity is defined. Also check kJ versus kcal, drained versus undrained quantities and whole-package versus per-item values. Convert or scale once, keeping the original information visible. A normalised comparison often resolves the apparent duplicate without requiring any assumption about the database’s overall quality.',
      ] },
      { id: 'source-selection', title: 'Resolve real discrepancies using the best matching source', paragraphs: [
        'If the food identity and portion basis match but the figures still differ, compare them with the current package or a clearly documented official food source. Products and preparations can differ, so a generic value may be a weaker match for a specific packaged item. Keep the source decision tied to this food rather than declaring every similar entry unreliable.',
        'For recurring foods, a small personal reference list can hold the chosen source and serving definition outside the app. That makes future checks quicker. If you switch products, revisit the reference. A consistent but outdated choice is not necessarily more useful than a newly checked entry that reflects what is actually in your kitchen.',
      ] },
      { id: 'diary-duplicates', title: 'Check accidental duplicates in the day itself', paragraphs: [
        'There is a second kind of duplicate: the same consumed food entered twice. It can happen when you copy a recent lunch and then manually add its components, or when a whole-meal entry remains beside a replacement calculation. Review the total in context before deciding that you ate much more than expected.',
        'Nexal supports free manual meal logging and copying recent meals on Android. After copying, check the meal quantities and whether you already recorded the food. Repeated names are not always errors: two separate coffees may be correct. Resolve entries by what you consumed and when, preserving real repeats while correcting accidental overlap through the available logging tools.',
      ] },
    ],
    feature: { href: '/calorie-macro-tracker', label: 'Keep your daily meal record clear', text: 'Use Nexal’s free manual tracking with a brief source and duplication check.' },
    sources: [{ href: '/calorie-macro-tracker', label: 'Nexal: calorie and macro tracking' }],
    faqs: [
      { question: 'Why do two entries for the same food show different calories?', answer: 'They may use different products, preparation states, energy units or serving sizes. Compare those details before concluding the nutrition data conflicts.' },
      { question: 'Should I remove every repeated food name from a diary?', answer: 'No. Repeated consumption can be correct. Remove overlap only when multiple entries describe the same food you ate once.' },
    ],
  },
  {
    ...publication,
    slug: 'weekly-food-diary-review',
    title: 'A weekly food diary review focused on useful information',
    metaTitle: 'Weekly Food Diary Review: A Simple Method',
    description: 'Review a week of meal records for coverage, repeated uncertainty and practical friction before drawing conclusions from averages.',
    category: 'NUTRITION TRACKING', readTime: '5 min read',
    intro: 'A weekly review can help you improve the diary itself before interpreting its totals. This method is for people who have several days of food records and want a useful next action, rather than a score for how well they ate.',
    takeaway: 'Check coverage and confidence before averages. Find one recurring source of missing or uncertain information, then make next week’s logging easier at that specific point.',
    sections: [
      { id: 'coverage', title: 'Check which days and meals are actually represented', paragraphs: [
        'Start by scanning the week for gaps. An empty dinner slot might mean no dinner was recorded, not that no dinner was eaten. A low day might be incomplete rather than genuinely different. Count the days with reasonably complete records before treating the full week as a comparable set of observations.',
        'Imagine Monday through Friday look detailed while Saturday contains breakfast alone and Sunday is blank. Dividing those totals by seven would create a number that mostly describes missing entries. You can review the recorded weekdays as weekdays, while acknowledging that they do not represent the entire week. Avoid filling gaps with invented meals just to produce a tidy average.',
      ] },
      { id: 'confidence', title: 'Distinguish measured entries from broad estimates', paragraphs: [
        'Review the sources behind unusually high or low meals. Was the lunch weighed, based on a package, copied from a previous day or estimated at a restaurant? These are different levels of information. A weekly sum can combine them, but the sum does not remove uncertainty from the underlying records.',
        'Choose one entry that looks surprising and inspect its units, serving basis and possible duplication. If the surprise disappears after correcting a kJ mix-up or repeated meal, the lesson concerns data entry. Do not turn a recording error into a reason to change future meals. Clear inputs should come before interpretation of any pattern.',
      ] },
      { id: 'friction-pattern', title: 'Find the recurring moment where logging becomes difficult', paragraphs: [
        'Look for patterns in the missing information. Perhaps workday lunches are clear but evening additions are often forgotten. Perhaps you remember restaurant mains while leaving drinks out. Name the practical obstacle as specifically as possible: labels discarded before logging, uncertainty about a shared dish, or waiting until bedtime to reconstruct the whole day.',
        'Pick one change connected to that obstacle. Keep the package until the entry is complete, capture a personal note after lunch or calculate a common sauce portion once. These are small workflow improvements. They are more testable than deciding to be more disciplined, because next week you can see whether the missing information appeared.',
      ] },
      { id: 'review-result', title: 'End with one next action and a limit on the conclusion', paragraphs: [
        'Write a brief review such as five workdays recorded; weekend incomplete; lunch drinks often missing; next week record the drink with the meal. This is enough to turn the diary into a learning tool. It avoids treating a short, uneven set of estimates as proof of a nutritional problem or a specific body outcome.',
        'Nexal offers free meal, calorie and macro logging on Android. Review the available records manually; this guide does not promise an automatic weekly audit or a special analytics report. If tracking feels burdensome, simplify the detail or reconsider whether it serves you. A useful weekly review should reduce confusion, not require a second demanding tracking system.',
      ] },
    ],
    feature: { href: '/calorie-macro-tracker', label: 'Build a food diary you can review', text: 'Start with Nexal’s free core meal tracking and inspect one week at a time.' },
    sources: [{ href: '/calorie-macro-tracker', label: 'Nexal: meal tracking and nutrition estimates' }],
    faqs: [
      { question: 'Should I average all seven days if some are incomplete?', answer: 'An average of incomplete days can be misleading. Review the coverage and state which days the calculation actually represents.' },
      { question: 'What should I change after a weekly review?', answer: 'Start with one practical issue in recording or planning. Do not infer a personal dietary prescription from uncertain diary totals.' },
    ],
  },
  {
    ...publication,
    slug: 'protein-tracking-without-prescribed-targets',
    title: 'Protein tracking without turning the diary into a prescribed target',
    metaTitle: 'Protein Tracking Without Prescribed Targets',
    description: 'Learn to record protein grams, scale label quantities and review the source of an entry without adopting an arbitrary daily target.',
    category: 'NUTRITION TRACKING', readTime: '5 min read',
    intro: 'You can learn how protein appears in your usual meals without deciding that every day must reach a number from an article. This guide is for people who want to understand the recording process and its limits before choosing whether detailed tracking is useful.',
    takeaway: 'Record the food and portion first, then calculate protein from a matching source. A diary total describes your entries; it does not establish your personal requirement.',
    sections: [
      { id: 'record-purpose', title: 'Give the tracking exercise a descriptive purpose', paragraphs: [
        'Choose a question the diary can answer, such as which usual meals contain the protein recorded on their labels, or whether your saved lunch entry matches the product you buy. These questions concern information quality. They do not require a daily prescription or an assumption that a higher number is always better.',
        'Avoid borrowing a target simply because a calculator or social post supplies one. Individual advice depends on circumstances that a short logging guide does not assess. If a clinician or dietitian has given you guidance, use their interpretation. Otherwise, begin by understanding the quantities you record rather than treating a few diary entries as evidence of a deficiency.',
      ] },
      { id: 'protein-maths', title: 'Scale protein grams using the actual food quantity', paragraphs: [
        'Suppose an invented food label lists 12 grams of protein per 100 grams. You eat 175 grams of that food. Multiply 12 by 1.75 to obtain 21 grams of protein for the portion. If a different label gives protein per serving, first calculate how many of those servings you consumed. The example demonstrates arithmetic, not a recommended portion.',
        'Keep food weight and protein weight distinct. Eating 175 grams of a food does not mean eating 175 grams of protein. For mixed meals, add the calculated protein contributions of the consumed components or use one suitable combined estimate. Do not use both methods for the same food and accidentally repeat its contribution.',
      ] },
      { id: 'source-matching', title: 'Check products and preparation before comparing meals', paragraphs: [
        'Two foods with similar names may have different protein values, so read the current label or use a clearly described food source. Check the brand, variety, raw or cooked state and serving basis. If your lunch uses a different filling from yesterday, a copied macro total may no longer describe it, even when the meal name is unchanged.',
        'For foods without labels, use an appropriate source and acknowledge that the portion may be estimated. A restaurant dish with unknown ingredients cannot support the same confidence as a measured packaged food. Compare your records at the level of certainty they provide rather than reading tiny numerical differences as meaningful changes in your diet.',
      ] },
      { id: 'review-without-score', title: 'Review the record without scoring your meals', paragraphs: [
        'At the end of a short recording period, ask whether the process answered your original question. You may discover that one entry used a whole-container protein value for a partial portion, or that a recurring lunch lacked a clear source. Correct those records before drawing any broader conclusion. The improvement is a more interpretable diary.',
        'Nexal’s free manual meal and macro logging on Android includes protein alongside calories, carbohydrates and fat. Premium AI estimates can assist with estimates, but they do not determine a personal protein requirement. If the recording process causes stress or dominates meal decisions, simplify it or stop. Understanding a label should support your choices rather than impose an arbitrary daily score.',
      ] },
    ],
    feature: { href: '/calorie-macro-tracker', label: 'Explore free protein and macro logging', text: 'Use Nexal to record the meals you eat without treating this guide as a target prescription.' },
    sources: [{ href: '/calorie-macro-tracker', label: 'Nexal: protein, carbohydrate and fat tracking' }],
    faqs: [
      { question: 'Does this guide recommend a daily protein target?', answer: 'No. It explains recording and portion calculations. Get qualified individual advice when you need a personal dietary target.' },
      { question: 'Is food weight the same as protein weight?', answer: 'No. Use the food’s stated protein value and scale it to the portion. The full weight of a food is not its protein content.' },
    ],
  },
  {
    ...publication,
    slug: 'meal-substitution-review',
    title: 'Review a meal substitution before keeping the original macro total',
    metaTitle: 'Meal Substitution Review: Check the New Meal',
    description: 'Check ingredient swaps for quantities, preparation, preferences and changed macros before updating a planned meal or copying an old entry.',
    category: 'MEAL PLANNING', readTime: '5 min read',
    intro: 'An ingredient substitution can rescue dinner when the shop is out of something, but the original nutrition line does not automatically transfer. This guide is for cooks adapting a meal plan and deciding what needs review before the replacement becomes part of the diary.',
    takeaway: 'A workable swap must suit the recipe and your preferences. Recalculate the changed component for its actual quantity, then record the meal you prepared rather than the original proposal.',
    sections: [
      { id: 'swap-purpose', title: 'Identify why you need a replacement', paragraphs: [
        'Name the constraint first: availability, cost, taste, equipment or an ingredient preference. That determines what a useful substitution must accomplish. A replacement that resembles the original macro total may still require equipment you do not have or introduce an ingredient you avoid. Review practical suitability before trying to preserve a particular number.',
        'If an allergy or medical restriction motivates the swap, check the current ingredient and allergen information and obtain appropriate advice. Do not treat an AI suggestion or a vegetarian label as safety confirmation. FSANZ’s food allergy guidance explains the role of declared allergens on labels; a meal planner is a different source of information.',
      ] },
      { id: 'quantity-example', title: 'Calculate the changed component on its own basis', paragraphs: [
        'Imagine the original recipe uses 100 grams of a product labelled with 10 grams of protein per 100 grams. Your replacement supplies 6 grams per 100 grams, and you use 150 grams. The original contribution was 10 grams of protein; the replacement contributes 9 grams. These figures are invented to demonstrate why both the product and quantity matter.',
        'Review calories, carbohydrate and fat using the replacement’s own figures too. Matching protein alone does not make the entire nutrition profile identical. You do not need to force an equivalent result; the diary simply needs to describe the adapted meal. Keep unchanged components separate so you can revise the relevant part without recalculating unrelated assumptions.',
      ] },
      { id: 'preparation-review', title: 'Check cooking method, yield and added ingredients', paragraphs: [
        'A substitute may change the way you prepare the dish. A dry ingredient and a ready-to-eat version require different quantity references. A replacement cooked in sauce may introduce an addition not present in the original method. Read the preparation instructions and account for what you actually use instead of comparing ingredient names alone.',
        'Suppose you replace a plain filling with a marinated one. Check whether the product values already include the marinade before adding a separate sauce estimate. If the finished batch makes a different number of portions, review your share as well. The original plan’s serving count is not evidence that your changed recipe produced identical plates.',
      ] },
      { id: 'update-record', title: 'Reconcile the plan with the actual meal', paragraphs: [
        'After cooking, confirm the replacement product, amount and portion consumed. Enter the revised calculation and make sure the original food has not remained as a second contribution. If you keep the substitution in a personal recipe note, describe what changed so that next time you do not have to reconstruct the decision from an old total.',
        'Nexal Premium offers AI meal planning; free manual meal and macro logging is available on Android. Review suggested substitutions yourself and record the actual meal through the supported workflow. This article does not promise automatic macro matching or automatic adjustments after a swap. A useful suggestion still needs a final check against your ingredients and quantities.',
      ] },
    ],
    feature: { href: '/ai-meal-planner', label: 'Explore planning ideas with Nexal Premium', text: 'Review AI meal suggestions, then log what you actually prepare using free core tracking.' },
    sources: [{ href: 'https://www.foodstandards.gov.au/consumer/foodallergies', label: 'FSANZ: food allergies and label information' }, { href: '/ai-meal-planner', label: 'Nexal: AI meal planning' }],
    faqs: [
      { question: 'Can I keep the original macros after an ingredient swap?', answer: 'Only if your reviewed calculation supports that result. A different product, amount or preparation can change the meal’s nutrition.' },
      { question: 'Does an AI substitution guarantee allergy safety?', answer: 'No. Check current labels and appropriate advice. A generated suggestion is not an allergen safety assessment.' },
    ],
  },
  {
    ...publication,
    slug: 'repeat-meals-without-autopilot-errors',
    title: 'Save time repeating meals without repeating yesterday’s errors',
    metaTitle: 'Repeat Meals Without Food Diary Errors',
    description: 'Reuse recent meals while checking changed products, quantities and additions, so a faster food diary still reflects today’s intake.',
    category: 'NUTRITION TRACKING', readTime: '5 min read',
    intro: 'Repeating a meal can make logging much faster, but copying also repeats every assumption in the original entry. This guide is for people with familiar breakfasts and lunches who want a quick check that keeps convenience from becoming an inaccurate record.',
    takeaway: 'Verify the original before reusing it, then check what changed today. Copying is a starting point for a meal record, not confirmation that the meal happened unchanged.',
    sections: [
      { id: 'source-entry', title: 'Choose a reliable original to repeat', paragraphs: [
        'Find a recent meal with a clear food source, quantity and calculation basis. If the original was a broad cafe estimate, copying it does not make it a measured homemade meal. If it contained a serving mistake, that mistake travels with it. Review the source once before making it the basis of a recurring routine.',
        'Imagine your usual lunch record includes two slices of bread, a filling and a spread. Check that the bread values describe the actual slices rather than the entire loaf, and that the spread appears once. A few minutes checking this original can prevent the same ambiguity from entering every subsequent workday through a convenient copy.',
      ] },
      { id: 'change-check', title: 'Use three questions before accepting the copied meal', paragraphs: [
        'Ask whether the products are the same, whether the amounts are the same and whether the additions are the same. These questions catch different changes. The meal can retain its familiar name while using a different brand, an extra slice or a sauce that was absent yesterday. Update only the relevant parts using the information you have.',
        'For example, Monday’s yoghurt bowl might use 150 grams of one product with fruit, while Tuesday’s uses 200 grams of another product without fruit. Copying saves you from starting with an empty record, but all three checks reveal something to revise. Do not leave the original fruit simply because it arrived with the copied entry.',
      ] },
      { id: 'consumption-check', title: 'Confirm today’s meal was actually consumed', paragraphs: [
        'A planned repeat is not a completed meal. You might pack your usual lunch and then buy something else, share part of it or save it for later. Reconcile the diary with the event after eating. Otherwise, the copied meal can remain beside the replacement, creating an accidental double record rather than merely a portion discrepancy.',
        'Do not bulk-fill missing days with a habitual meal when you cannot remember what happened. A repeated pattern can inform a rough reconstruction, but it should be acknowledged as uncertain. For useful comparisons, distinguish meals known to match from days filled by assumption. Complete-looking records are not necessarily more informative than clearly identified gaps.',
      ] },
      { id: 'nexal-repeat', title: 'Make a brief review part of the shortcut', paragraphs: [
        'Nexal supports copying recent meals as part of its free Android meal logging. Use that feature for a genuinely repeated meal, then review the quantities and nutrition when ingredients change. Premium AI meal planning, macro estimates and barcode scanning are separate options; they are not necessary simply to repeat a recent meal record.',
        'Set a practical stopping point: confirm products, amounts, additions and consumption, then save the entry. You do not need to re-investigate an unchanged packaged food every time for ordinary logging, but review it when the package or recipe changes. The shortcut works best when a small check is built into it rather than postponed to a weekly correction session.',
      ] },
    ],
    feature: { href: '/calorie-macro-tracker', label: 'Try free recent-meal copying in Nexal', text: 'Save routine logging effort while checking that copied meals match today.' },
    sources: [{ href: '/calorie-macro-tracker', label: 'Nexal: free core meal tracking' }],
    faqs: [
      { question: 'Is copying a recent meal free in Nexal?', answer: 'Yes. Copying recent meals is available with core meal tracking; it does not require AI meal planning.' },
      { question: 'What should I check after copying?', answer: 'Review products, quantities, additions and actual consumption, then make sure the same meal has not already been entered elsewhere in the day.' },
    ],
  },
  {
    ...publication,
    slug: 'workout-and-nutrition-weekly-review',
    title: 'Review workouts and nutrition together without inventing cause and effect',
    metaTitle: 'Workout and Nutrition Weekly Review',
    description: 'Review meal records and workout history together, check coverage and scheduling friction, and choose one practical change for next week.',
    category: 'NUTRITION TRACKING', readTime: '5 min read',
    intro: 'Keeping food and training in one app can make a weekly review convenient, but two records appearing together do not explain why a session felt different. This guide is for people who want to compare their routines while keeping conclusions proportionate to the evidence.',
    takeaway: 'Check what was recorded, notice practical scheduling patterns and choose one organisational change. A short diary cannot isolate the dietary cause of a workout result.',
    sections: [
      { id: 'parallel-records', title: 'Begin with coverage in both records', paragraphs: [
        'Look at the available workout history and food entries for the same calendar week. Which sessions were logged, and which food days are reasonably complete? A completed training log beside an empty dinner record does not demonstrate that you trained without eating. It demonstrates that the two records have different coverage.',
        'Keep intended workouts separate from completed work. If the plan listed three sessions and the history shows two, begin with those two actual sessions. Likewise, a planned meal is not evidence of consumption. Matching the records to real events gives the review a fair starting point before you look for any relationship between food and training.',
      ] },
      { id: 'comparable-sessions', title: 'Compare similar sessions and preserve context', paragraphs: [
        'A different exercise, machine or workout duration can change how two sessions look on paper. Avoid interpreting every difference as improvement or decline. Read the logged exercise details and completed work first. Context such as a different schedule may help explain the record, but it does not prove a single cause for how you performed.',
        'Suppose Monday’s session followed an ordinary workday while Thursday’s was squeezed between travel commitments. The diary might also show different meals. That is enough to ask whether the week was practical, but not enough to conclude that a particular carbohydrate figure caused the session outcome. Several circumstances changed together, and your records may not capture all of them.',
      ] },
      { id: 'organisation-pattern', title: 'Look for a problem you can actually act on', paragraphs: [
        'Useful findings often concern organisation: you forget to log meals after late training, the planned gym slot conflicts with commuting, or you run out of time to prepare a packed dinner. Name that specific friction. It offers a realistic next action without requiring you to diagnose a physiological reason for a difficult workout.',
        'For example, if evening training repeatedly leaves food entries unfinished, try capturing a short personal meal note before clearing the plate. If your workout log is rushed, review the recording workflow for a familiar session. These changes improve information and scheduling. They are different from prescribing more training or changing food quantities because one week felt uneven.',
      ] },
      { id: 'next-week', title: 'Write one next-week action and keep advice within scope', paragraphs: [
        'Finish with a brief statement of the evidence and action: two workouts recorded; food records incomplete after both; next week confirm dinner entries before bedtime. If the issue concerns exercise technique, pain or individual dietary needs, seek suitable qualified guidance. A combined app view does not provide an assessment of those concerns.',
        'Nexal offers free manual workout logging, custom workouts and workout history alongside free meal and macro logging on Android. Premium AI workout and meal planning are optional. Review those records yourself; this guide does not claim automatic dietary adjustments, exercise-calorie balancing or causal analytics. The value of the combined review is a clearer account of your week and a manageable next step.',
      ] },
    ],
    feature: { href: '/workout-meal-planner-app', label: 'Explore workouts and meals in one Android app', text: 'Use Nexal’s free core records for a practical weekly review, with optional Premium planning.' },
    sources: [{ href: '/workout-meal-planner-app', label: 'Nexal: workout and meal tracking together' }],
    faqs: [
      { question: 'Can a weekly review prove a meal caused a better workout?', answer: 'No. A short set of food and training records cannot isolate cause and effect, especially when other circumstances and recording quality vary.' },
      { question: 'Does this review need Premium?', answer: 'No. Core workout logging, custom workouts, history and manual meal tracking are free. AI planning is optional.' },
    ],
  },
  {
    ...publication,
    slug: 'fitness-app-signup-email-verification',
    title: 'Fitness app signup and email verification: get into the right account',
    metaTitle: 'Fitness App Signup & Email Verification',
    description: 'Troubleshoot fitness app signup, missing verification emails and account confusion on Android without sharing passwords or making duplicate purchases.',
    category: 'CHOOSING AN APP', readTime: '4 min read',
    intro: 'Getting stuck between signup, an email link and the app can turn a simple download into a confusing start. This checklist is for Android users who need to identify where account setup stopped, not bypass authentication or purchase access a second time.',
    takeaway: 'Keep the signup method, email address and app account consistent. Check the exact verification step before creating another account, and keep subscription billing separate from sign-in troubleshooting.',
    sections: [
      { id: 'identify-account', title: 'Identify the account you are trying to create', paragraphs: [
        'Start with the sign-in method you selected. Email and password, Google sign-in and the Google account used for a store purchase are not interchangeable labels. Write down which method you used and the account address, keeping that note private. When returning to the app, choose the same method rather than starting another signup because the first screen looks familiar.',
        'Check the address for a typing error before requesting another message. A missing character or the wrong email domain can explain why nothing reaches your inbox. If you used a Google account selector, confirm the selected account rather than assuming it chose the address you normally use. Do not post account screenshots containing private information in a public review.',
      ] },
      { id: 'missing-email', title: 'If the confirmation email has not arrived', paragraphs: [
        'Search your mailbox for the app name and check spam or other filtered folders. Make sure you are looking at the inbox associated with the signup address, not a second account on the phone. If the app provides a resend option, use that option and avoid repeatedly submitting the form while a message is still being delivered.',
        'Treat a confirmation link as private account information. Open the most recent message you requested after checking that it matches the signup you initiated. Do not forward the link to another person or paste it into a support ticket. An old, expired or already-used link may require a fresh request through the app rather than repeated clicks.',
      ] },
      { id: 'return-to-app', title: 'If the email link opens a browser instead of the app', paragraphs: [
        'Read the page result before deciding what to do next. A success message, expired-link warning and connection error describe different situations. If the page confirms the step, return to the installed app and try signing in through the original method. This checklist does not promise that every browser will automatically hand the link back to every Android app.',
        'If the result is unclear, keep the non-sensitive error wording and note whether you opened the message on the same phone. Check your connection and that you installed the current app from its official Google Play listing. Avoid repeated account creation or reinstalling as a first response; neither identifies whether the existing account was confirmed successfully.',
      ] },
      { id: 'access-and-support', title: 'Separate account access from subscription access', paragraphs: [
        'Reaching an account and having a Premium entitlement are separate checks. If Google Play already shows a successful purchase but the app still asks you to subscribe, do not buy again to troubleshoot sign-in. Keep the purchasing account and receipt available privately, and contact support through the official app or store listing with the problem description.',
        'Nexal offers free core manual workout and meal tracking; AI planning and other Premium tools are optional. For Nexal account help, contact support@nexal.app with your app version, Android version and the exact step that failed. Never send passwords, verification links, one-time codes or full payment details. Share sensitive purchase identifiers only through an appropriate official support process when needed.',
      ] },
    ],
    feature: { href: '/workout-meal-planner-app', label: 'See Nexal’s Android tracking tools', text: 'Get the official Android app and choose the tracking tools that fit your routine.' },
    sources: [{ href: '/about', label: 'Nexal: product information and official support' }, { href: '/privacy', label: 'Nexal: account information and privacy choices' }],
    faqs: [
      { question: 'Should I create another account if verification seems stuck?', answer: 'First check the original address, signup method and latest requested email. Creating another account can make it harder to locate the records or access you expected.' },
      { question: 'Should I subscribe again if a purchase succeeded but access is missing?', answer: 'No. Keep the receipt private and contact official support to investigate account and entitlement access rather than creating a second purchase.' },
    ],
  },
];
