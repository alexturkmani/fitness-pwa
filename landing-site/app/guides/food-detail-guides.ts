import type { Guide } from './content';

const publication = { publishedAt: '2026-10-11', contentBatch: 'expansion-2' };

export const foodDetailGuides: Guide[] = [
  {
    ...publication,
    slug: 'tinned-food-drained-versus-net-weight',
    title: 'Drained versus net weight: logging the part of a tin you eat',
    metaTitle: 'Tinned Food: Drained vs Net Weight',
    description: 'Match tinned food nutrition to drained solids or retained liquid, calculate your share and try free manual meal logging in Nexal.',
    category: 'NUTRITION TRACKING', readTime: '5 min read',
    intro: 'A tin can show a net weight, a drained weight and a serving size that refers to only one of them. For beans, fish or fruit, the useful question is which contents reached your plate. Choosing the largest printed weight without reading the nutrition basis can misdescribe an otherwise simple lunch.',
    takeaway: 'Identify what the panel describes before using either weight. Keep solids, discarded liquid and liquid eaten with the meal distinct, and record only your portion.',
    sections: [
      { id: 'three-quantities', title: 'Read three quantities as separate pieces of information', paragraphs: [
        'Net contents can include the food and its packing liquid, while a stated drained weight describes the drained contents. Neither number alone establishes the nutrition calculation. Look beside the panel for words such as drained, as sold or including sauce. Keep the serving definition with the nutrient values rather than pairing a weight from the front with an unrelated column.',
        'The Australian Food Standards Code addresses nutrition information for drained foods. For your diary, use the wording on the particular product rather than assuming every tin follows the same basis. Beans packed in water, fish in oil and a ready-to-eat sauced product require separate checks. If the basis is unclear, consult the manufacturer before treating your calculation as label-based.',
      ] },
      { id: 'half-tin', title: 'Calculate a drained share without using the net weight', paragraphs: [
        'Here is an explicitly illustrative example: a tin has 400 grams net contents, 240 grams drained contents and 8 grams of protein per 100 grams drained. You eat 120 grams of the drained food. Your calculation is 120 divided by 100, multiplied by 8, giving 9.6 grams of protein. The 400-gram figure has no role in this drained-solids calculation.',
        'Apply the same 1.2 multiplier to each relevant value in that drained column. Eating half the drained contents does not mean eating 200 grams merely because the unopened tin lists 400 grams. If your actual drained yield differs, use your measured edible amount with the matching nutrition basis rather than forcing the portion to match the package illustration.',
      ] },
      { id: 'liquid-decision', title: 'Decide whether the packing liquid belongs in the meal', paragraphs: [
        'Before discarding the tin, ask whether you drained it, retained everything or used some liquid in a sauce. If the nutrition describes drained solids but you also consume packing liquid, the solids calculation may not describe the complete meal. Seek suitable information for the retained component. Do not invent an exact oil or syrup contribution from the difference between net and drained weights.',
        'Conversely, adding a separate sauce entry may duplicate nutrition when the product panel already describes the whole sauced food. Make one clear boundary: this entry includes these contents. Keep a brief companion note outside the app when the label needs explanation. A useful note says drained solids only or all contents retained, rather than an unsupported precise drainage percentage.',
      ] },
      { id: 'tin-workflow', title: 'Test this label check with one real lunch in Nexal', paragraphs: [
        'Download Nexal for Android, create your account and try free manual meal and macro tracking with a tin you regularly buy. Read the panel, calculate the portion outside the app and enter its nutrition through the available manual workflow. Keep the packaging reference in your own notes if needed; this process does not require a dedicated drained-weight calculator.',
        'When copying that recent meal, confirm both the product and how you handled its liquid. A lunch made with the whole tin differs from one made with half the drained contents. Premium barcode scanning is optional and still needs this check. Finish when the food, label basis and eaten quantity agree, without expecting the download to make uncertain packing-liquid estimates exact.',
      ] },
    ],
    feature: { href: '/calorie-macro-tracker', label: 'Try Nexal’s free manual meal tracker', text: 'Record the edible portion after checking the tin’s nutrition basis.' },
    sources: [{ href: 'https://www.legislation.gov.au/F2015L00395/latest/text', label: 'Australian Food Standards Code: nutrition information requirements' }],
    faqs: [
      { question: 'Should I always use the drained weight?', answer: 'Use it when you ate drained contents and the nutrition values describe drained food. Other products or retained liquid may need a different basis.' },
      { question: 'Does scanning a tin establish how much liquid I consumed?', answer: 'No. Nexal Premium barcode scanning does not establish your portion or how you drained the food. Check those details yourself.' },
    ],
  },
  {
    ...publication,
    slug: 'dry-versus-prepared-cereal-mix-labels',
    title: 'Dry versus prepared nutrition labels for cereal and mixes',
    metaTitle: 'Dry vs Prepared Cereal and Mix Labels',
    description: 'Check whether cereal and mix labels include milk or other additions before calculating your portion and logging it in Nexal.',
    category: 'NUTRITION TRACKING', readTime: '5 min read',
    intro: 'A cereal box or powdered mix may present nutrition for the dry product, a prepared serving or both. The prepared picture can look like your breakfast while using a different milk quantity or preparation method. Read the column heading before transferring its figures into a food diary.',
    takeaway: 'Choose either a complete matching prepared calculation or a dry-product calculation plus your actual additions. Combining both approaches can count the same milk twice.',
    sections: [
      { id: 'preparation-footnote', title: 'Find the preparation footnote before picking a column', paragraphs: [
        'Look for as sold, as prepared, reconstituted or a statement naming added ingredients. Write down the dry serving quantity and the preparation recipe separately. A per-100-gram figure for dry powder cannot be applied to 100 grams of the finished bowl. The label may also describe a specific milk type, so a visually similar breakfast is not necessarily a match.',
        'Australian nutrition requirements distinguish foods reconstituted with water and foods prepared with other ingredients. Use the current product instructions to identify what its values include. This guide concerns the boundary of a labelled calculation, rather than a general raw-to-cooked conversion. Adding water changes the finished quantity; adding milk introduces nutrition that must be accounted for somewhere.',
      ] },
      { id: 'choose-route', title: 'Choose the calculation route that matches your bowl', paragraphs: [
        'Use the prepared values only when your ingredient amounts and preparation match their stated assumptions closely enough for your purpose. Otherwise, start with the dry-product values and calculate the additions separately. This is especially useful when you change the liquid, use a different quantity of powder or add a topping. Keep each addition attached to its own product information.',
        'If the package only supplies prepared information and your method differs, look for dry-product information from the manufacturer. Do not casually subtract generic milk nutrition from a prepared total when the label used an unspecified recipe. That creates another assumption rather than recovering the original formulation. A clearly acknowledged estimate is more understandable than an apparently exact reverse calculation.',
      ] },
      { id: 'bowl-example', title: 'Work through a cereal bowl with different milk', paragraphs: [
        'In this illustrative example, 40 grams of dry cereal contributes 150 kcal, and your chosen milk contributes 50 kcal per 100 millilitres. You use 160 millilitres, which contributes 80 kcal. The bowl therefore totals 230 kcal before toppings. These invented figures demonstrate component accounting; they are not product values or a suggested breakfast portion.',
        'Suppose the box also shows 250 kcal for a prepared serving with another milk amount. Do not enter 250 and then add your 80 kcal of milk. Instead, use the checked 150 plus 80 calculation and scale protein, carbohydrate and fat using their own label values. The difference from the box is explained by preparation, not automatically by a label error.',
      ] },
      { id: 'repeat-breakfast', title: 'Keep repeated breakfasts tied to their preparation', paragraphs: [
        'Try the bowl in Nexal’s free Android manual meal tracker after downloading the app and creating an account. Calculate the components outside the app, then record the meal nutrition. A short companion note can hold the dry quantity, milk product and milk volume. Keep that reference yourself rather than assuming Nexal stores a preparation recipe or converts dry powder automatically.',
        'Copying a recent meal can save work when breakfast repeats, but review the preparation before accepting the same totals. A water-based version, a milk-based version and a larger dry serving need separate calculations. Premium AI macro estimates are optional; they do not remove the need to check which additions the original label already includes. Test the manual routine with your actual breakfast first.',
      ] },
    ],
    feature: { href: '/calorie-macro-tracker', label: 'Log your actual breakfast with Nexal', text: 'Use free manual tracking for a checked cereal or mix calculation.' },
    sources: [{ href: 'https://www.legislation.gov.au/F2015L00395/latest/text', label: 'Australian Food Standards Code: reconstituted and prepared nutrition information' }],
    faqs: [
      { question: 'Does prepared always mean prepared with milk?', answer: 'No. Read the product’s preparation instructions and nutrition footnotes. The prepared basis may use water or specified additional ingredients.' },
      { question: 'Should I add milk after using prepared nutrition?', answer: 'Only if the prepared values exclude that milk. Otherwise use a matching complete prepared entry or calculate dry product and actual additions separately.' },
    ],
  },
  {
    ...publication,
    slug: 'log-drinks-milk-sugar-cup-sizes',
    title: 'Logging drinks when milk, sugar and cup sizes change',
    metaTitle: 'Log Drinks with Milk, Sugar and Cup Changes',
    description: 'Separate drink size from milk and sugar quantities, handle refills and log changing coffee or tea orders with Nexal’s free tracker.',
    category: 'NUTRITION TRACKING', readTime: '5 min read',
    intro: 'Your usual coffee can change several times in one day: a small cup at home, a larger cafe order and a topped-up mug at work. A drink name alone does not capture those differences. Build a drink record around the ingredients consumed, then use the cup description as context.',
    takeaway: 'Check milk quantity, sweetener additions and refills separately. A larger cup does not prove that every ingredient increased in the same proportion.',
    sections: [
      { id: 'cup-versus-recipe', title: 'Separate cup capacity from the drink recipe', paragraphs: [
        'Cup size describes a container, not necessarily the milk it holds. A mug of tea with a splash of milk and a milk-based coffee can use similarly sized cups with very different ingredient amounts. For a cafe drink, ask about the order size and milk choice or use the cafe’s own information when it describes that specific order.',
        'For homemade drinks, measure your ordinary milk addition once if that feels useful, then keep it as a reference rather than treating the mug capacity as milk volume. Packaged milk values are commonly stated per 100 millilitres. FSANZ explains the per-volume panel basis. Match that basis to the milk poured, including any additional pour after the first sip.',
      ] },
      { id: 'sweetener-check', title: 'Give sugar and syrup their own quantity check', paragraphs: [
        'Distinguish sugar, a flavoured syrup and an alternative sweetener using their own packaging. A spoon, sachet and pump are not universal quantities. If the packet states a weight, use it. If a cafe provides values for the finished sweetened drink, establish whether those already include the standard syrup before adding another estimate for the same ingredient.',
        'Use an explicitly illustrative calculation: milk contributes 60 kcal per 100 millilitres and you pour 80 millilitres, giving 48 kcal. A labelled sugar sachet contributes an invented 16 kcal, so those additions total 64 kcal. Account separately for the base drink where relevant. These figures demonstrate the method and should not be used as values for every coffee order.',
      ] },
      { id: 'refill-ledger', title: 'Treat a refill as a new consumption event', paragraphs: [
        'A refill may be water only, another complete drink or an extra milk pour. Record what changed rather than copying the entire first cup automatically. For example, topping up tea with water does not mean repeating the original milk and sugar. Making another cup with fresh additions does. This distinction is more useful than naming every event one large mug.',
        'Keep a quick companion tally on paper or in your own notes during a busy morning: first cup with milk and sugar; water refill; second cup with milk only. Translate that into the diary later. The tally is a memory aid outside Nexal, not an automatic refill detector. Close it once the consumed ingredients are represented, including any drink left unfinished.',
      ] },
      { id: 'drink-test', title: 'Try a changing-drink day before relying on a shortcut', paragraphs: [
        'Install Nexal on Android, create your account and use free manual meal and macro logging for a familiar drink. Enter the calculated nutrition through the supported workflow. When the order repeats, copying a recent meal can help, but check the size and additions before keeping the old figures. Save the reference calculation separately if you need ingredient-level context.',
        'Test the shortcut against three actual situations: the usual home cup, a different cafe size and a refill. You need a routine that distinguishes them without lengthy reconstruction. Premium AI macro estimates and barcode scanning are optional, and neither establishes an unobserved milk pour. Choose the workflow by whether it helps you describe your drinks clearly, rather than expecting a cup name to settle the arithmetic.',
      ] },
    ],
    feature: { href: '/calorie-macro-tracker', label: 'Try free drink logging in Nexal', text: 'Record changing drink additions with a simple manual meal workflow.' },
    sources: [{ href: 'https://www.foodstandards.gov.au/consumer/labelling/panels', label: 'FSANZ: nutrition information per serving and per 100 mL' }],
    faqs: [
      { question: 'Can I double a small coffee entry for a large one?', answer: 'Only when the actual recipe doubles. Cup sizes, milk amounts and syrup quantities may change in different proportions.' },
      { question: 'Should a water top-up repeat the original milk entry?', answer: 'No. Account for new ingredients consumed. A water-only top-up does not repeat milk or sugar already logged.' },
    ],
  },
  {
    ...publication,
    slug: 'food-diary-midnight-overnight-meals',
    title: 'Food diary midnight boundaries: trace one overnight meal once',
    metaTitle: 'Food Diary Midnight Boundaries for Night Meals',
    description: 'Use a dated event trail to reconcile overnight meals across midnight, check saved dates and avoid counting the same food twice in Nexal.',
    category: 'NUTRITION TRACKING', readTime: '5 min read',
    intro: 'Dinner after midnight may feel like the end of yesterday even when the calendar says today. That mismatch can create missing meals or duplicate entries when you review a night out or an overnight shift. Start with an event trail that separates when food was eaten from when you entered it.',
    takeaway: 'Give each eating event one date-and-time reference and one diary contribution. Verify the app’s date behaviour, and keep your personal shift interpretation in companion notes.',
    sections: [
      { id: 'two-clocks', title: 'Separate consumption time from logging time', paragraphs: [
        'Write down the local date and approximate time of eating, then the time you recorded it if that differs. A meal eaten at 00:30 and entered after waking at 14:00 is one event. The entry time should not make you reconstruct a second lunch. An overnight work label can connect events for your own review without replacing their calendar references.',
        'NIDDK’s sample food and beverage diary records times and describes foods, including portion details, providing a useful model for an event record. The boundary method here is an organisational convention, not a claim about which date food biologically belongs to. If another person needs your diary in a particular format, agree the reporting convention rather than silently changing dates to match your sleep schedule.',
      ] },
      { id: 'overnight-trace', title: 'Trace a single shift across two calendar dates', paragraphs: [
        'Consider this illustrative timeline: a packed meal at 23:15 on Sunday, a snack at 01:10 on Monday and breakfast at 07:00 on Monday. Keep three distinct events in your companion note. You can label the group Sunday night shift for context while still distinguishing Sunday’s meal from Monday’s snack and breakfast. The dates and times are examples, not a meal schedule.',
        'At review, match each event to one saved contribution. Do not copy the whole shift into Monday because the shift ended there if Sunday’s meal is already represented. Equally, a blank Sunday-night summary does not establish missing intake if you are looking at the wrong date. Inspect the adjacent day before filling what appears to be a gap.',
      ] },
      { id: 'midnight-test', title: 'Check the saved date before building a long record', paragraphs: [
        'On your phone, inspect how the diary assigns dates when you log near midnight and when you record a previous meal later. Check any available date controls directly. This guide does not assume a custom day-start setting, automatic shift grouping or particular timezone behaviour. If a date requirement matters to you, verify that workflow before depending on it.',
        'For an uncertain old entry, consult your event note rather than moving it simply because a daily total looks unusual. Correct only what you can identify through the available controls. If the app cannot represent the reporting convention you need, retain an external explanation. Do not duplicate a meal on both dates as a workaround for a display or interpretation problem.',
      ] },
      { id: 'overnight-nexal', title: 'Evaluate Nexal with an ordinary overnight record', paragraphs: [
        'Download Nexal for Android, create an account and test free manual meal and macro logging across a relevant overnight period. Keep your event trail separately and compare it with saved records. Copy recent meals only for food actually eaten again. The practical test is whether you can explain each event the next day without relying on the daily total alone.',
        'Premium AI meal planning is optional and does not resolve a date convention for you. Before closing the review, check the previous date, current date and any meals entered retrospectively. Preserve real repeated snacks while removing only confirmed overlap through supported controls. A useful diary follows what happened; it does not need every overnight calendar day to resemble a daytime routine.',
      ] },
    ],
    feature: { href: '/workout-meal-planner-app', label: 'Test Nexal’s Android diary around your routine', text: 'Try free meal records and verify saved dates with one overnight event trail.' },
    sources: [{ href: 'https://www.niddk.nih.gov/health-information/weight-management/healthy-eating-physical-activity-for-life/health-tips-for-adults', label: 'NIDDK: sample food and beverage diary with time and amount' }],
    faqs: [
      { question: 'Should all food from one night shift go on one date?', answer: 'Choose a clear reporting convention, but keep actual dates in your reference notes. Verify what the app supports and represent each meal only once.' },
      { question: 'Does Nexal automatically group meals by shift?', answer: 'This guide does not claim automatic shift grouping or a custom midnight boundary. Check date behaviour directly and keep shift context in companion notes.' },
    ],
  },
  {
    ...publication,
    slug: 'log-leftovers-recipe-changes-second-day',
    title: 'Recording leftovers when yesterday’s recipe changes today',
    metaTitle: 'Log Leftovers When the Recipe Changes',
    description: 'Carry forward only the remaining food, account for second-day additions and log a changed leftover meal without rebuilding yesterday in Nexal.',
    category: 'NUTRITION TRACKING', readTime: '5 min read',
    intro: 'Yesterday’s stew becomes today’s pasta sauce, or leftover rice gets eggs and vegetables. Copying yesterday’s meal unchanged misses the new ingredients, while recalculating the whole original batch can count food already eaten. Treat the remaining food as a new starting quantity with its own additions.',
    takeaway: 'Carry forward the estimated nutrition of the food remaining, add what enters today’s dish and calculate today’s eaten share. Keep yesterday’s consumed portion in yesterday’s record.',
    sections: [
      { id: 'carry-forward', title: 'Establish what remains before changing the dish', paragraphs: [
        'Find the original batch calculation and identify the portion left after the first meal. If the dish was reasonably uniform, its remaining share can provide a starting estimate. If someone took most of one ingredient, the leftover mixture may differ. State that limitation rather than assuming the remaining weight contains exactly the original balance of ingredients.',
        'Keep a companion recipe note outside the app with original batch totals, the amount consumed and the estimated remainder. That is an accounting reference, not proof of food safety or a storage recommendation. Follow appropriate food handling guidance separately. Your diary calculation should not make a decision about whether leftovers are suitable to eat.',
      ] },
      { id: 'changed-batch', title: 'Build today’s batch from the remainder and additions', paragraphs: [
        'Use this explicitly illustrative example: yesterday’s batch was estimated at 1,200 kcal and half remained, so the starting remainder contributes 600 kcal. Today you add ingredients totalling 300 kcal. The changed batch therefore contains an estimated 900 kcal. If you eat one third of an evenly mixed new batch, today’s portion contributes 300 kcal. These are invented arithmetic figures.',
        'Scale protein, carbohydrate and fat separately using the same shares. Do not enter yesterday’s entire 1,200 kcal alongside today’s additions because half the old batch was already eaten. If you eat all the remainder yourself with the additions, there is no new portion division. The calculation follows food entering the new dish and the share leaving it onto your plate.',
      ] },
      { id: 'second-day-yield', title: 'Check whether reheating changed weight or composition', paragraphs: [
        'Reheating, adding water or reducing a sauce can change finished weight. FSANZ explains that moisture gains and losses affect recipe weight. Use the current finished batch weight when dividing today’s dish by weight; yesterday’s grams-per-serving reference may no longer describe it. Water added for texture changes the division basis without adding the nutrition of another ingredient.',
        'If you add distinct components, such as separate pasta and a leftover sauce, calculate their portions separately instead of assuming a uniform mixture. If you discard part of the dish or its liquid, avoid inventing exact nutrient losses from discarded weight alone. Record a reasonable estimate with its limitation. A changed recipe deserves a new explanation, even when its familiar name stays the same.',
      ] },
      { id: 'leftover-entry', title: 'Use recent-meal copying as a reference, then revise', paragraphs: [
        'Try Nexal’s free manual meal tracker on Android after downloading the app and creating your account. Calculate the new portion outside the app, then record its nutrition. A copied recent meal can provide a starting reference, but check every carried-forward amount and addition before treating it as today’s meal. Do not also log the old ingredients for the same consumed food.',
        'Keep the two versions clear in your companion notes: original batch and second-day batch. Nexal Premium AI meal plans or macro estimates are optional; this method does not require automatic leftover detection or recipe versioning. Before saving, ask whether today’s total includes only today’s eaten share and whether any remaining food still belongs to a future meal rather than this one.',
      ] },
    ],
    feature: { href: '/calorie-macro-tracker', label: 'Record changed leftover meals in Nexal', text: 'Use free manual tracking for today’s portion after calculating the new batch.' },
    sources: [{ href: 'https://www.foodstandards.gov.au/business/labelling/nutrition-panel-calculator/weight-change-factors', label: 'FSANZ: recipe weight changes through moisture gains and losses' }],
    faqs: [
      { question: 'Can I copy yesterday’s leftovers entry unchanged?', answer: 'Only if today’s food and portion still match. New ingredients, a different remaining share or a changed preparation can require a new calculation.' },
      { question: 'Does adding water mean adding calories?', answer: 'Water changes the dish’s weight without adding food energy. Recalculate weight-based shares of the finished dish rather than reusing the old portion weight.' },
    ],
  },
  {
    ...publication,
    slug: 'food-diary-decimal-rounding-false-precision',
    title: 'Food diary decimals: round calculations without inventing precision',
    metaTitle: 'Food Diary Rounding and False Precision',
    description: 'Keep useful decimals during portion calculations, round the final entry sensibly and avoid mistaking extra digits for better food data.',
    category: 'NUTRITION TRACKING', readTime: '5 min read',
    intro: 'A calculator can produce six decimal places from a label with whole numbers. Those digits describe the arithmetic, not a laboratory measurement of your lunch. For people calculating portions manually, a simple rounding convention makes entries readable without losing useful information halfway through the calculation.',
    takeaway: 'Keep intermediate arithmetic intact, round once at the final entry and spend your checking effort on food identity and portion basis before tiny decimal differences.',
    sections: [
      { id: 'input-limits', title: 'Recognise the precision already missing from the inputs', paragraphs: [
        'Nutrition information describes food using stated quantities and label conventions. US FDA guidance includes rounding rules for displayed nutrient values. A printed whole number is therefore not necessarily an unrounded measurement. Applying a precise portion multiplier cannot recover hidden digits or establish the exact composition of the particular item you ate.',
        'Your portion may also be approximate: an uneven scoop, food left on a plate or a scale reading to whole grams. Keep those limits in perspective when comparing days. The difference between a measured portion and an estimated bowl is usually a more useful explanation than the final hundredth of a gram. Extra digits should not disguise uncertain ingredients or preparation.',
      ] },
      { id: 'one-round', title: 'Round at the end of a multi-step calculation', paragraphs: [
        'Consider an explicitly illustrative product with 7.3 grams of protein per 100 grams. A 37-gram portion contributes 7.3 multiplied by 0.37, or 2.701 grams. If your final-entry convention uses one decimal place, record 2.7 grams. The calculation is transparent even though the entry is shorter. These invented numbers are arithmetic examples, not measured nutrition for a real product.',
        'If several components form one meal, add their calculated contributions before rounding the final total. Rounding each small component to a whole gram first can move the combined figure unnecessarily. Use the available entry precision and a consistent convention suitable for ordinary diary use. You are simplifying the displayed estimate, not claiming that one decimal place is universally the right resolution.',
      ] },
      { id: 'mismatch-triage', title: 'Investigate the right kind of discrepancy', paragraphs: [
        'When two calculations disagree, check the unit, serving definition, food state and product version first. A per-serving figure mistaken for per-100-gram nutrition can create a substantial mismatch. A tiny difference between two valid calculation routes may reflect rounding in the original columns. Do not change your portion simply to make two printed columns reconcile perfectly.',
        'Use a short decision sequence in your companion notes: same food; same preparation; same quantity basis; then rounding. If those first checks fail, resolve them before debating decimal places. If they pass and the remaining difference is small, keep your chosen source and method clear. This makes repeated entries consistent without presenting them as exact chemical measurements.',
      ] },
      { id: 'readable-nexal', title: 'Test a readable manual entry in Nexal', paragraphs: [
        'Download Nexal on Android, create an account and try its free manual meal and macro logging with one calculated portion. Keep the original label and arithmetic in a companion note if you want an audit trail. Enter the final meal values through the supported fields. This guide does not assume adjustable decimal display settings or an automatic rounding control in the app.',
        'Copy a recent meal when it truly repeats, preserving the checked calculation rather than recalculating it with a different rounding rule each time. Premium AI macro estimates remain estimates even if they display precise-looking numbers. Before finishing, ask whether your entry communicates the source and portion honestly. A tidy number can be useful without implying that your diary measures intake to a fraction of a gram.',
      ] },
    ],
    feature: { href: '/calorie-macro-tracker', label: 'Keep readable meal records with Nexal', text: 'Try free manual logging after checking your portion arithmetic.' },
    sources: [{ href: 'https://www.fda.gov/regulatory-information/search-fda-guidance-documents/guidance-industry-guide-developing-and-using-data-bases-nutrition-labeling', label: 'FDA: nutrition label values and rounding guidance' }],
    faqs: [
      { question: 'Do more decimal places mean a more accurate diary?', answer: 'No. Extra digits may only reflect calculator output. Accuracy still depends on the food source, portion and assumptions.' },
      { question: 'Should I round every ingredient before adding it?', answer: 'Keep intermediate calculations intact where practical, add the component contributions and round the final value to a sensible supported precision.' },
    ],
  },
  {
    ...publication,
    slug: 'logging-food-by-piece-varying-sizes',
    title: 'Logging food bought by the piece when pieces vary in size',
    metaTitle: 'Log Food by Piece When Sizes Vary',
    description: 'Handle bakery items and loose produce sold by piece using product information, edible weights or clearly described portion estimates in Nexal.',
    category: 'NUTRITION TRACKING', readTime: '5 min read',
    intro: 'One bread roll, one apple and one pastry are convenient shopping quantities, but they are not fixed weights. A shop can charge by the piece while the nutrition source describes an average item. Your diary needs a bridge between the item you bought and the quantity its nutrition values represent.',
    takeaway: 'Use a verified per-item figure when it matches, an edible weight when available or a clearly described size estimate. Do not treat the receipt’s item count as a nutrition measurement.',
    sections: [
      { id: 'seller-information', title: 'Check whether one item has a defined nutrition basis', paragraphs: [
        'Start with information from the seller or package for that exact product. A standard bakery roll with a stated weight and recipe gives you a stronger reference than a generic roll name. If the shop offers several sizes, identify which one its values describe. A price ticket or receipt showing one item confirms a purchase, not the nutritional quantity of the food.',
        'FSANZ notes that unpackaged foods and foods made and packaged at the point of sale may not require a nutrition information panel. Missing figures therefore do not establish that an item contains no relevant nutrition. Ask for product information if available, then choose a clearly described alternative source when needed. Keep the uncertainty attached to this item rather than every future purchase.',
      ] },
      { id: 'edible-weight', title: 'Use edible weight when a per-100-gram source fits', paragraphs: [
        'For a suitable matching source, weigh the edible portion rather than packaging or material you will discard. With loose fruit, account for peel or cores consistently with the source description. For bakery food, identify filling and toppings as part of the product match. Weighing an unknown filled pastry does not make nutrition for a plain roll appropriate.',
        'Here is an explicitly illustrative calculation: a matched roll source lists 250 kcal per 100 grams. Your edible roll weighs 86 grams, giving 215 kcal. Another roll weighing 64 grams would give 160 kcal on that same basis. These invented figures show why one piece can represent different quantities; they do not establish actual nutrition for a bakery product.',
      ] },
      { id: 'no-scale', title: 'Choose a useful description when weighing is impractical', paragraphs: [
        'If you cannot weigh the item, use a source with a defined size or stated item weight and acknowledge the match is approximate. A companion note such as large filled roll from the corner bakery preserves more context than one roll alone. You can use that note when reviewing the estimate later without inventing a weight you never measured.',
        'For recurring purchases, check a representative item when practical, then decide whether later pieces look comparable enough for your diary purpose. A visibly larger item, new filling or different supplier warrants review. Do not create a permanent rule that every piece from a shop has one exact weight. The reference helps reduce repeated work while leaving room for real variation.',
      ] },
      { id: 'piece-entry', title: 'Try the method on one regular purchase in Nexal', paragraphs: [
        'Install Nexal for Android and create your account to test free manual meal and macro logging. Calculate the item’s estimated nutrition from your chosen source, then enter the consumed portion through the supported workflow. Keep seller details, assumed size or measured weight in your own companion notes. This does not require automatic size recognition or an item-weighing feature in Nexal.',
        'When copying a recent meal, compare the new piece with the original reference before accepting its nutrition. If the source or size changes, revise the calculation instead of copying purely by food name. Premium AI macro estimates are optional and cannot turn an unspecified piece into a measured portion. Judge the app by whether this simple routine fits your actual shopping habits.',
      ] },
    ],
    feature: { href: '/calorie-macro-tracker', label: 'Try a practical food diary in Nexal', text: 'Use free manual tracking for measured or clearly estimated individual items.' },
    sources: [{ href: 'https://www.foodstandards.gov.au/consumer/labelling/panels', label: 'FSANZ: nutrition panels and exemptions for some unpackaged foods' }],
    faqs: [
      { question: 'Is one piece a reliable serving unit?', answer: 'It can be when the item and its nutrition definition are consistent. Variable sizes or recipes require a weight or an acknowledged estimate.' },
      { question: 'Must I weigh every item sold individually?', answer: 'No. A matching seller value or a clearly described size estimate may be sufficient for your purpose. Keep its uncertainty visible in companion notes.' },
    ],
  },
  {
    ...publication,
    slug: 'buffet-meal-estimate-transparent-food-diary',
    title: 'Record a buffet meal estimate you can explain afterwards',
    metaTitle: 'Log a Buffet Meal Estimate Transparently',
    description: 'Reconstruct buffet rounds, separate known foods from uncertain dishes and save one transparent meal estimate with Nexal’s manual tracker.',
    category: 'NUTRITION TRACKING', readTime: '5 min read',
    intro: 'A buffet is a sequence of small decisions rather than one fixed restaurant order. You might revisit a dish, share dessert and leave part of a serving. A transparent estimate begins with the rounds you actually ate, so the final total can be explained without pretending the buffet supplied a weighed recipe.',
    takeaway: 'Reconstruct each round once, identify the uncertain components and choose a reasonable working estimate. Keep the uncertainty in companion notes rather than polishing unsupported decimal places.',
    sections: [
      { id: 'rounds-first', title: 'Reconstruct the meal by rounds before calculating', paragraphs: [
        'Make a brief companion note after eating: first plate, second plate, dessert and drinks. Describe the foods and approximate amounts you consumed, including unfinished items. This prevents one mental image of the first plate from standing in for the whole visit. You do not need to document other diners or interrupt the meal with a detailed weighing exercise.',
        'NIDDK’s sample diary illustrates recording eating events, including shared and unfinished food. Use that event-based idea for the buffet, while keeping the calculation method separate. Your note is outside Nexal and is not an automatic plate tracker. If a later round is poorly remembered, identify that gap instead of replacing it with a confidently named standard buffet meal.',
      ] },
      { id: 'source-confidence', title: 'Separate known portions from uncertain mixed dishes', paragraphs: [
        'Use the venue’s information when it clearly describes the food and quantity you ate. A wrapped item with its own label may be easier to estimate than an unfamiliar mixed casserole. For an unlabelled dish, choose a reasonably matching prepared-food source and explain the key assumption. A sauce-heavy preparation should not silently inherit values for a plain ingredient.',
        'Keep practical confidence categories in your note: labelled item; recognisable food with estimated amount; mixed dish with uncertain recipe. These are your review categories, not promised app fields. Spend attention on the part most likely to change the estimate, such as an unknown dressing or a repeated serving. Recalculating a known packaged item repeatedly will not settle an unknown mixed dish.',
      ] },
      { id: 'working-total', title: 'Choose a working estimate without hiding the uncertainty', paragraphs: [
        'Consider an explicitly illustrative reconstruction: known items contribute 300 kcal, while two uncertain dishes together are estimated at 400 to 600 kcal. That gives an arithmetic range of 700 to 900 kcal. A working entry of about 800 kcal could be reasonable if your chosen assumptions support it. These invented figures are not typical buffet values or a recommended intake.',
        'The range is a scenario comparison, not a statistical confidence interval. Keep its basis outside the app and enter a single working estimate through the available workflow. Estimate protein, carbohydrate and fat from the selected components separately; an energy range alone cannot determine them. Do not choose an endpoint simply to make the day’s total look more comfortable or more cautious.',
      ] },
      { id: 'buffet-close', title: 'Save one reviewed meal and stop reconstructing it', paragraphs: [
        'Try Nexal’s free manual meal and macro logging on Android after downloading the app and creating an account. Enter either the calculated meal total or its component contributions as appropriate to the workflow, without counting both. Keep your rounds note available for one final check: repeated servings represented, shared food limited to your share and drinks included where relevant.',
        'Premium AI macro estimates are optional starting points, not verification of hidden buffet recipes. Once your best available reconstruction is represented, close the entry. A later correction is useful when new information arrives, such as the venue identifying a dish, but repeated guessing does not necessarily improve it. The app’s useful role is maintaining an understandable record, without guaranteeing an exact total from uncertain portions.',
      ] },
    ],
    feature: { href: '/calorie-macro-tracker', label: 'Keep a transparent buffet record in Nexal', text: 'Try free manual logging with your own clearly stated portion assumptions.' },
    sources: [{ href: 'https://www.niddk.nih.gov/health-information/weight-management/healthy-eating-physical-activity-for-life/health-tips-for-adults', label: 'NIDDK: sample diary including shared and unfinished meals' }],
    faqs: [
      { question: 'Should I enter a range for the buffet meal?', answer: 'Use a range in companion notes to explore uncertain assumptions, then record a supported working estimate. This guide does not claim a range-entry field in Nexal.' },
      { question: 'Can an AI estimate verify buffet ingredients?', answer: 'No. Nexal Premium AI macro estimates do not establish an unknown recipe or the amount you consumed. Review the assumptions yourself.' },
    ],
  },
  {
    ...publication,
    slug: 'food-product-recipe-packaging-changes-logging',
    title: 'Logging a familiar product after its recipe or packaging changes',
    metaTitle: 'Log Food After Recipe or Packaging Changes',
    description: 'Compare current and previous package information, distinguish portion changes from reformulation and update future Nexal entries carefully.',
    category: 'NUTRITION TRACKING', readTime: '5 min read',
    intro: 'A familiar product can arrive in a smaller tub, use a revised serving size or announce a new recipe. Each change has different implications for a repeated diary entry. Compare the package you are eating now with your old reference before deciding whether the nutrition calculation needs updating.',
    takeaway: 'Review product identity, package amount, serving basis and nutrition per common quantity separately. Update new consumption using the current pack without rewriting well-supported old meals.',
    sections: [
      { id: 'change-classification', title: 'Identify what changed before replacing your reference', paragraphs: [
        'Check the exact variety and product name first, then package quantity, serving size, ingredient list and nutrition panel. New artwork alone does not prove a recipe change. Conversely, familiar artwork does not establish that the contents remain identical. Your task is to identify a relevant difference in the product information, not infer a nutritional effect from the advertising design.',
        'FSANZ explains how ingredient lists identify ingredients and characterising components. Use the current list as part of the product check, alongside the panel rather than as a substitute for it. If you avoid particular ingredients, review the current label on its own terms. A saved diary record or previous purchase does not verify the contents of a new pack.',
      ] },
      { id: 'common-basis', title: 'Compare nutrition on the same quantity basis', paragraphs: [
        'Use this explicitly illustrative comparison: an old panel lists 120 kcal per 100 grams and a new one lists 96 kcal per 80-gram serving. Dividing 96 by 80 and multiplying by 100 gives 120 kcal per 100 grams. The apparent reduction is explained by serving size, not a lower energy concentration. These invented figures demonstrate label comparison only.',
        'Now suppose the new package is 160 grams rather than the old 200 grams. Eating a whole pack changes from an illustrative 240 kcal to 192 kcal on the same per-100-gram basis. The recipe could be unchanged while the whole-pack entry needs revision. Compare protein, carbohydrate and fat on a common basis too, before deciding that the formulation itself changed.',
      ] },
      { id: 'reference-boundary', title: 'Start a new reference without erasing old context', paragraphs: [
        'Keep the current pack details in your companion reference and identify when you began using it. If an older meal genuinely used the older package, its original calculation may still be the right historical record. Do not apply the new serving size backwards merely to make all entries uniform. Only correct old entries when you have evidence that their original basis was wrong.',
        'If you no longer have the older package, avoid reconstructing its recipe from memory. Mark the old reference as uncertain and use the current label for current meals. For a clear mismatch in scanned information, check the physical package or manufacturer’s information before accepting it. Successful product recognition does not by itself establish that a nutrition result describes this version.',
      ] },
      { id: 'updated-copy', title: 'Recheck the next copied meal in Nexal', paragraphs: [
        'Download Nexal for Android, create your account and try free manual meal and macro tracking using the current pack. Calculate your portion from its current panel and enter those values through the supported workflow. Keep package-version context in your own notes. This guide does not depend on automatic reformulation alerts or a product-version database in Nexal.',
        'Before copying a recent meal, ask whether its package amount and calculation basis still match. If they do not, revise today’s entry rather than carrying forward the old whole-pack total. Premium barcode scanning is optional and should be checked against the pack. One careful update can provide a useful new reference without treating every older record as an error.',
      ] },
    ],
    feature: { href: '/calorie-macro-tracker', label: 'Update your everyday food record in Nexal', text: 'Use free manual meal tracking with the label from the product you actually ate.' },
    sources: [{ href: 'https://www.foodstandards.gov.au/consumer/labelling/ingredients', label: 'FSANZ: ingredient lists and percentage labelling' }, { href: 'https://www.foodstandards.gov.au/consumer/labelling/panels', label: 'FSANZ: nutrition panel serving and quantity bases' }],
    faqs: [
      { question: 'Does a smaller per-serving number prove reformulation?', answer: 'No. First compare the serving sizes and convert to a common quantity. A different serving alone can explain the change.' },
      { question: 'Should I rewrite all past entries using the new label?', answer: 'No. Keep historical meals tied to the product actually consumed, and correct older records only when you have evidence of an error.' },
    ],
  },
  {
    ...publication,
    slug: 'meal-plan-ingredient-availability-grocery-swaps',
    title: 'Meal plan ingredient availability: reconcile the plan with the shop',
    metaTitle: 'Meal Plan Availability and Grocery Swaps',
    description: 'Check a meal plan against pantry stock, pack sizes and store availability, make practical manual swaps and record the meals actually prepared.',
    category: 'MEAL PLANNING', readTime: '5 min read',
    intro: 'A meal plan can be neatly written while depending on ingredients your shop does not stock or quantities that leave awkward leftovers. Before cooking, reconcile the plan with the groceries you can actually buy. This is a basket-and-kitchen workflow, rather than a search for nutritionally identical substitutes.',
    takeaway: 'Check stock and usable quantities first, choose a feasible manual change and carry that decision through shopping, preparation and logging. A generated meal remains a proposal until you cook it.',
    sections: [
      { id: 'availability-pass', title: 'Run a pantry and shop availability pass', paragraphs: [
        'Write a companion shopping note with four columns of information: ingredient needed, amount required, amount already available and amount to buy. Keep it in your usual notes or on paper. Check what usable form you have, such as dry, cooked or frozen, before treating pantry stock as a direct match. Count ingredients across the planned meals rather than checking each dinner in isolation.',
        'Then identify uncertain purchases: a specific brand, seasonal produce or an ingredient carried by only one shop. Decide which meals can tolerate a change and which rely on that ingredient for their basic preparation. This is a manual planning exercise outside Nexal. Do not assume AI suggestions have checked local stock, store inventory or the contents of your cupboards.',
      ] },
      { id: 'pack-reality', title: 'Turn recipe amounts into a workable grocery basket', paragraphs: [
        'Consider an explicitly illustrative plan needing 300 grams of an ingredient across two dinners. Your pantry contains 100 usable grams, and the shop sells 250-gram packs. Buying one pack covers the remaining 200 grams and leaves 50 grams to account for. These invented quantities demonstrate purchasing arithmetic; they are not a serving recommendation or a claim about any store’s packaging.',
        'Give that remainder a plausible use, keep it for another suitable purpose or reconsider the recipe before buying. The right answer need not be exact pack matching. Likewise, changing to a larger substitute pack does not mean the entire pack belongs in tonight’s diary. Shopping quantity, recipe quantity and eaten quantity are three separate decisions, even when an app brings planning and tracking together.',
      ] },
      { id: 'swap-decision', title: 'Choose a manual swap by its job in the recipe', paragraphs: [
        'Identify what the missing ingredient does: provides a filling, forms a sauce, adds texture or acts as a topping. Compare replacements for availability, preparation time, equipment and taste before trying to preserve a macro total. Sometimes changing the meal entirely is more practical than assembling several substitutes that create another difficult shopping list.',
        'Check current ingredient and allergen information for the actual replacement. FSANZ explains declared allergen information and requesting information for unpackaged food. A suggested swap does not verify those details for the product in your basket. Update the companion plan with the chosen product and amount so that you cook the revision, rather than discovering the decision again when dinner begins.',
      ] },
      { id: 'basket-to-diary', title: 'Bring the actual cooked meal into Nexal', paragraphs: [
        'Nexal Premium offers AI meal planning; download the Android app, create your account and evaluate whether planning ideas help with your real kitchen constraints. Review availability yourself before shopping. After preparing the revised dish, use free manual meal and macro logging for what you actually ate, calculating changed ingredients outside the app as needed.',
        'Keep the original suggestion, shopping revision and actual meal distinct in your companion notes. This workflow does not rely on automatic grocery lists, store connections or automatic macro adjustments after a swap. A useful final review asks what was unavailable, what you changed and whether the replacement made cooking easier. That gives the next planning attempt concrete information without promising that every generated recipe will fit your shop.',
      ] },
    ],
    feature: { href: '/ai-meal-planner', label: 'Explore Nexal’s optional AI meal plans', text: 'Review Premium planning ideas against your groceries, then log actual meals with free manual tracking.' },
    sources: [{ href: 'https://www.foodstandards.gov.au/consumer/labelling/allergen-labelling', label: 'FSANZ: allergen information on packaged and unpackaged food' }],
    faqs: [
      { question: 'Does Nexal check whether my shop stocks an ingredient?', answer: 'This guide does not claim store inventory access. Check availability yourself and keep your grocery decisions in companion notes.' },
      { question: 'Should I log the quantity bought or the quantity eaten?', answer: 'Log your eaten portion. A purchased pack can cover several recipes or leave unused food, so its full contents are not automatically one meal.' },
    ],
  },
  {
    ...publication,
    slug: 'logging-packaged-meal-kits-label-totals',
    title: 'Logging packaged meal kits: ingredient packs or finished meal totals?',
    metaTitle: 'Log Meal Kits: Packs vs Finished Meal Totals',
    description: 'Check what a meal-kit label includes, account for optional extras and choose finished-dish or component totals before logging dinner in Nexal.',
    category: 'NUTRITION TRACKING', readTime: '5 min read',
    intro: 'A packaged meal kit might contain wraps, seasoning and sauce while asking you to supply the filling. Its nutrition panel may describe the supplied contents or an assembled meal using specified additions. Before logging dinner, establish what the printed total includes so that the ingredient packs and finished dish do not overlap.',
    takeaway: 'Choose one accounting boundary: a matching assembled-meal total or a calculation from components and actual extras. Check supplied packs, required additions, optional additions and your eaten share separately.',
    sections: [
      { id: 'kit-boundary', title: 'Map the label to the contents and instructions', paragraphs: [
        'Lay out the supplied packs and read the nutrition heading, serving count and preparation footnotes. Identify whether the panel refers to the unopened kit, one component, one prepared serving or the full assembled dish. A picture showing a complete dinner does not settle this. Australian nutrition requirements distinguish prepared-food information, so the wording beside the values matters.',
        'Make a companion checklist outside the app: supplied ingredients; required ingredients you provide; optional extras; quantities actually used. If each inner pack also has a panel, those figures do not necessarily belong alongside the outer box total. They may describe ingredients already included in it. Contact the manufacturer if the total’s boundary remains unclear after reading the instructions.',
      ] },
      { id: 'choose-kit-route', title: 'Choose assembled totals only when the assumptions match', paragraphs: [
        'A finished-meal figure can be convenient when you follow the specified recipe, use the stated quantities and eat the matching share. Check whether optional cheese, oil, garnish or sauce is included before adding it. If your filling or amounts differ materially from the stated preparation, calculating the supplied contents and your actual additions may be easier to explain.',
        'Use component figures only where you have a clear source for each relevant ingredient. If the outer panel already describes all supplied contents, it can be the starting contribution for those contents without separately adding every sachet. Do not combine a full assembled-dish entry with supplied-pack entries merely because both sets of values are visible on the packaging.',
      ] },
      { id: 'kit-example', title: 'Work through an illustrative kit with an optional topping', paragraphs: [
        'Suppose the supplied kit contributes 800 kcal, your required filling contributes 600 kcal and an optional topping contributes 200 kcal. The assembled batch totals 1,600 kcal if you use everything. If the dish is divided into four equivalent portions and you eat one, your contribution is 400 kcal. All figures and serving counts here are explicitly illustrative, not real product nutrition.',
        'Now suppose a separate prepared panel already lists 350 kcal per portion for the supplied kit plus that filling, but excludes the topping. Adding one quarter of the topping, 50 kcal, also gives 400 kcal. Choose that route or the component route. Entering 350 plus a share of the kit and filling again would overlap the same ingredients. Calculate each macro on the corresponding basis too.',
      ] },
      { id: 'kit-nexal', title: 'Log your assembled portion after checking unused packs', paragraphs: [
        'Before saving, account for ingredients left unused and confirm your actual share. A sealed sauce sachet saved for another day does not belong in today’s consumed total simply because the box included it. Unequal portions may need a component estimate rather than equal division. Keep the accounting explanation with your own recipe notes; it is not an automatic meal-kit calculation in Nexal.',
        'Download Nexal for Android, create your account and try free manual meal and macro logging with the checked portion totals. Copy a recent meal only when the next kit and preparation match. Premium barcode scanning can help identify a product but still requires checking the panel boundary. The useful first test is one assembled meal represented once, including actual extras and excluding ingredients you did not consume.',
      ] },
    ],
    feature: { href: '/calorie-macro-tracker', label: 'Log an assembled meal-kit portion in Nexal', text: 'Try free manual tracking with a clear boundary around included ingredients and extras.' },
    sources: [{ href: 'https://www.legislation.gov.au/F2015L00395/latest/text', label: 'Australian Food Standards Code: nutrition information for food as prepared' }],
    faqs: [
      { question: 'Does a meal-kit panel always include ingredients I supply?', answer: 'No. Read the panel heading and preparation assumptions. It may describe supplied contents or a prepared recipe with specified additions.' },
      { question: 'Should I log inner packs as well as the finished meal?', answer: 'Choose one route. If the finished-meal total already includes those packs, adding them again would count the same food twice.' },
    ],
  },
  {
    ...publication,
    slug: 'two-minute-macro-tracker-logging-workflow',
    title: 'Macro tracker time cost: build a two-minute logging workflow',
    metaTitle: 'Build a Two-Minute Macro Logging Workflow',
    description: 'Time a routine meal entry, separate one-time setup from daily work and test a practical two-minute workflow with Nexal’s free manual tracker.',
    category: 'NUTRITION TRACKING', readTime: '5 min read',
    intro: 'If every meal turns into a research project, a food diary may be asking more time than you want to give it. A two-minute workflow is a design target for familiar meals, not a promise about every entry. Find which tasks repeat and which genuinely need fresh information before trying to log faster.',
    takeaway: 'Do the source check once for a stable meal, capture changes at eating time and close the entry after a short review. Measure the routine on your phone rather than assuming an advertised shortcut saves time.',
    sections: [
      { id: 'time-audit', title: 'Separate setup work from recurring entry work', paragraphs: [
        'Time one ordinary meal entry and note where the minutes go: locating the package, deciding the portion, calculating nutrition, entering values or checking what you already recorded. Keep the timing note outside the app. A first-time recipe calculation is different from logging an unchanged packaged lunch; combining them into one average can hide the part you can actually simplify.',
        'Choose a familiar test meal with a source you can verify. Prepare its calculation and reference details once, then treat that preparation as setup rather than claiming it fits inside two minutes. A changed product or uncertain restaurant meal may need longer. The aim is reducing unnecessary repeat work, not forcing every meal through the same speed requirement.',
      ] },
      { id: 'time-budget', title: 'Give the two minutes a specific job sequence', paragraphs: [
        'Use this explicitly illustrative time budget: 20 seconds to identify the meal, 40 seconds to check changed amounts and additions, 40 seconds to enter or copy and revise, and 20 seconds to confirm the saved contribution. These intervals total two minutes, but they are planning allocations rather than measured Nexal performance or a guarantee for every phone and user.',
        'Test the sequence with your actual meal. If finding the reference repeatedly consumes the first minute, make that reference easier to access in companion notes. If arithmetic is the slow step, pre-check the unchanged meal and calculate only the changed component. Do not skip portion verification merely to meet the timer. A slightly longer correct workflow may be more useful than a fast unexplained total.',
      ] },
      { id: 'capture-and-stop', title: 'Capture the details that disappear, then use a stopping rule', paragraphs: [
        'Record a brief personal cue before disposing of packaging or clearing the plate when you cannot log immediately. Include the food, amount and unusual addition rather than a long narrative. NIDDK’s diary example shows how time and food descriptions can provide context. Your cue should preserve enough information to finish later, without turning the companion note into a second full diary.',
        'Use a stopping rule: source matches, portion reviewed, additions represented and entry saved once. Reopen the calculation when new information justifies it, not simply because the final digits feel uncertain. If logging consistently takes more time than it is worth to you, simplify the level of detail or reconsider the routine. Speed is a personal workflow choice, not a measure of dietary success.',
      ] },
      { id: 'nexal-time-test', title: 'Test free logging before choosing paid shortcuts', paragraphs: [
        'Download Nexal on Android, create an account and try free manual meal and macro tracking. Copying recent meals is also free and can reduce repeated entry work for meals that actually match. Time several ordinary attempts after setup, reviewing products, amounts and additions each time. Keep the results in your own note rather than assuming the app reports time spent logging.',
        'If the remaining friction is identifying packaged products or getting a starting estimate, evaluate Premium barcode scanning or AI macro estimates against that specific problem. Premium AI meal plans address planning, which is a different time cost. Choose based on whether the feature shortens your real routine after review, without expecting any paid tool to eliminate checking or guarantee a two-minute entry.',
      ] },
    ],
    feature: { href: '/calorie-macro-tracker', label: 'Time a familiar meal entry in Nexal', text: 'Try free manual tracking and recent-meal copying before evaluating optional Premium tools.' },
    sources: [{ href: 'https://www.niddk.nih.gov/health-information/weight-management/healthy-eating-physical-activity-for-life/health-tips-for-adults', label: 'NIDDK: food and beverage diary example' }],
    faqs: [
      { question: 'Does Nexal guarantee a two-minute meal entry?', answer: 'No. Two minutes is an illustrative workflow target. Actual time depends on the meal, information available and your entry process.' },
      { question: 'Do I need Premium to copy a familiar meal?', answer: 'No. Recent-meal copying and core manual meal tracking are free. Check that the copied meal matches what you ate today.' },
    ],
  },
  {
    ...publication,
    slug: 'shared-platter-personal-portion-notes',
    title: 'Portion notes for shared platters without measuring everyone',
    metaTitle: 'Shared Platter Portion Notes for Your Diary',
    description: 'Track your own bites, dips and second helpings from shared platters using practical companion notes and Nexal’s free manual meal logging.',
    category: 'NUTRITION TRACKING', readTime: '5 min read',
    intro: 'Shared platters keep changing as people take bread, dips, skewers or small bites over a long conversation. Dividing the platter by the number of diners rarely describes your own intake. A personal portion note can preserve enough detail to estimate your share without weighing the table or documenting what everyone else ate.',
    takeaway: 'Track your own transfers from the platter, using counts for identifiable pieces and a separate estimate for dips or mixed bites. Treat the result as a personal estimate rather than an equal share of the order.',
    sections: [
      { id: 'personal-boundary', title: 'Use your plate or eating events as the boundary', paragraphs: [
        'Start with what you served yourself, then account for second helpings and unfinished food. If you eat directly from the platter, recall the identifiable items you took rather than trying to reconstruct everyone’s consumption. Paying for one quarter of an order does not establish eating one quarter of it. The diary concerns your food, not a division of the bill.',
        'Keep the social meal comfortable by choosing a lightweight reference. A quick companion note after a serving can say two small skewers, bread and a spoonful of dip. That note stays outside Nexal unless you choose an available way to preserve it. This method does not need a shared household diary, diner profiles or a tool that measures the other people at the table.',
      ] },
      { id: 'count-and-scoop', title: 'Separate countable pieces from variable scoops', paragraphs: [
        'For similar identifiable pieces, use the count with a source that describes the same product and size. For torn bread, spooned dips or mixed bites, use an acknowledged portion estimate instead. Do not give every item the same confidence simply because it came on one board. A supplier’s description or label can improve a match but may not define the actual scoop you took.',
        'FSANZ explains that some unpackaged foods do not require nutrition panels, so a shared platter may lack item-specific figures. Use suitable information where available and state the weaker matches in your own notes. The most useful distinction may be between a verified packaged cracker and an unknown dip recipe, rather than calculating both to several decimal places.',
      ] },
      { id: 'personal-example', title: 'Build a personal total without dividing the whole board', paragraphs: [
        'Here is an explicitly illustrative note: three small pieces estimated at 60 kcal each, bread estimated at 100 kcal and dip estimated at 70 kcal. The working total is 350 kcal. These invented values demonstrate adding your own components, not nutrition for a typical platter. If one piece remained uneaten, revise the count before recording the total.',
        'Do not also add one quarter of the whole board after summing those components. Likewise, keep a later bread refill distinct from the bread already represented. Estimate each macro using the selected food references rather than deriving all macros from the energy total. A companion note explaining unknown dip quantity is more honest than pretending a rough scoop was measured precisely.',
      ] },
      { id: 'platter-entry', title: 'Turn the personal note into one reviewed diary contribution', paragraphs: [
        'Install Nexal for Android, create an account and try free manual meal and macro logging after the shared meal. Calculate your working portion outside the app, then use either its total or component contributions through the supported workflow. Review whether refills, dips and unfinished food are reflected. Do not include another diner’s food merely because it appeared on your side of the board.',
        'Premium AI macro estimates are optional and cannot establish who ate which portion of a shared platter. Keep the final entry at the confidence your observation supports. You can stop once your known pieces and reasonable estimates are represented. The practical app test is whether this personal routine is manageable, without turning a social meal into a measurement exercise for everyone present.',
      ] },
    ],
    feature: { href: '/calorie-macro-tracker', label: 'Log your own platter portion in Nexal', text: 'Try free manual meal tracking with a personal portion estimate.' },
    sources: [{ href: 'https://www.foodstandards.gov.au/consumer/labelling/panels', label: 'FSANZ: nutrition information and unpackaged-food exceptions' }],
    faqs: [
      { question: 'Can I divide the platter total by the number of diners?', answer: 'Only if that reasonably describes your actual share. Shared platters often have unequal portions, refills and different ingredient choices.' },
      { question: 'Do I need to record what everyone else ate?', answer: 'No. Focus on your own servings and unfinished food, keeping uncertain scoops or recipes clearly described in companion notes.' },
    ],
  },
  {
    ...publication,
    slug: 'per-container-versus-per-serving-food-labels',
    title: 'Read per-container versus per-serving labels before logging food',
    metaTitle: 'Per Container vs Per Serving Food Labels',
    description: 'Check dual-column labels, inner packs and partial containers before scaling nutrition to your actual portion in Nexal’s free meal tracker.',
    category: 'NUTRITION TRACKING', readTime: '5 min read',
    intro: 'A convenient container can hold more than one labelled serving, and a label may show both per-serving and per-container figures. The visual size of the pack does not decide which column to use. Start by identifying the container named by the panel and the share of that container you actually ate.',
    takeaway: 'Read the column heading and servings count together. Use the per-container total directly for that whole container, or scale the per-serving figures to your actual share, without multiplying twice.',
    sections: [
      { id: 'container-identity', title: 'Identify which container the label describes', paragraphs: [
        'Check whether the panel refers to the outer carton, one inner pot or an individually wrapped item. A multipack can place several layers of packaging around the food, so one pack is an ambiguous diary unit. Read the net amount, serving size and servings per container as a set. Keep the nutrition values attached to the same level of packaging.',
        'The US FDA explains that labels can provide both per-serving and per-package information. Other label formats may instead provide per-serving and per-100-gram columns. Identify the actual heading rather than expecting the rightmost column to mean the whole pack everywhere. A container marketed for convenience is not necessarily one serving, and a stated serving is not a personal instruction about how much to eat.',
      ] },
      { id: 'dual-column-example', title: 'Choose one route through a dual-column label', paragraphs: [
        'Consider an explicitly illustrative container listing three servings, 140 kcal per serving and 420 kcal per container. Eating the whole container means 420 kcal. You can obtain that directly from the container column or multiply 140 by three. Do not multiply 420 by three, because the whole-container figure already includes those servings. These are invented values for label arithmetic.',
        'If you eat half the container, the corresponding estimate is 210 kcal, or one and a half labelled servings. Scale protein, carbohydrate and fat using the same chosen portion basis. If actual printed columns differ slightly after multiplication, check the quantities first and allow for possible rounding rather than changing your eaten share to make every displayed number match.',
      ] },
      { id: 'partial-pack', title: 'Handle shared containers and leftovers at the right level', paragraphs: [
        'A whole-container value applies to all its contents, not automatically the food on your plate. If you share the pack or save part, estimate your consumed fraction or use a matching weight. For a reasonably uniform product, a measured half has a clearer basis than a visual claim of almost all. Uneven products may require checking the component mix as well.',
        'Keep a companion note identifying the container and remaining amount when that helps with the next meal. For example, outer carton has four pots; panel describes one pot is a useful packaging reference. Do not enter both one pot and one quarter of the carton if they describe the same food. Packaging hierarchy should clarify the calculation rather than create extra contributions.',
      ] },
      { id: 'container-nexal', title: 'Read the saved entry as a complete quantity statement', paragraphs: [
        'Download Nexal for Android, create your account and try free manual meal and macro logging using a product with a clear container definition. Calculate the nutrition for your share outside the app and enter the resulting values. Keep the original packaging reference in your own notes if needed. This guide does not assume every label column is automatically interpreted by the app.',
        'Before copying a recent meal, check whether the current pack has the same size and serving count. Premium barcode scanning is optional and does not establish how much of the container you ate. Finish by reading the record as a sentence: this product, this container reference, this consumed share. That check catches a whole-package figure accidentally treated as a single-serving figure before it becomes a recurring error.',
      ] },
    ],
    feature: { href: '/calorie-macro-tracker', label: 'Check a packaged-food entry in Nexal', text: 'Use free manual tracking after matching the label column to your portion.' },
    sources: [{ href: 'https://www.fda.gov/food/nutrition-facts-label/how-understand-and-use-nutrition-facts-label', label: 'FDA: serving information and dual-column nutrition labels' }],
    faqs: [
      { question: 'Should I multiply per-container calories by the serving count?', answer: 'No. A per-container figure already describes that container. Multiply per-serving values by the number of servings eaten instead.' },
      { question: 'Does the outer carton count as one container?', answer: 'Read the panel’s definition. A multipack may provide nutrition for an inner item rather than every item in the outer carton.' },
    ],
  },
  {
    ...publication,
    slug: 'food-diary-travel-ounces-grams-conversions',
    title: 'Food diary travel units: convert ounces, grams and servings clearly',
    metaTitle: 'Travel Food Diary: Ounces, Grams and Servings',
    description: 'Distinguish weight ounces from fluid ounces, use label gram equivalents and convert travel portions before logging meals in Nexal.',
    category: 'NUTRITION TRACKING', readTime: '5 min read',
    intro: 'An overseas label can describe a serving in ounces while your usual diary reference uses grams. A drink may use fluid ounces, and a familiar cup can have a different stated volume. Before converting anything, identify whether the quantity describes mass, liquid volume or a manufacturer’s serving definition.',
    takeaway: 'Prefer the gram or millilitre equivalent printed on the actual label. Otherwise convert within the same kind of quantity, then scale the nutrition once to the amount you consumed.',
    sections: [
      { id: 'unit-identity', title: 'Identify weight ounces and fluid ounces separately', paragraphs: [
        'An ordinary food weight ounce, written oz, converts to approximately 28.35 grams. A fluid ounce, written fl oz, measures volume rather than mass. NIST lists these as separate conversions; its approximate US fluid-ounce conversion is 29.57 millilitres. Do not multiply a drink’s fluid ounces by the weight-ounce factor and assume the result describes grams of that drink.',
        'Fluid-ounce conventions can differ by context, so use the metric quantity on the package when available. Similarly, treat cup as a label-defined measure rather than assuming your home measuring cup matches it. If the label says one cup with an accompanying gram amount, that gram amount is often the clearer bridge to a weighed food portion during travel.',
      ] },
      { id: 'printed-equivalent', title: 'Use the printed serving equivalent before a generic conversion', paragraphs: [
        'A label might pair a familiar unit with a rounded metric serving. For scaling that panel, keep its stated serving definition together rather than replacing the metric number with a more precise conversion and wondering why the columns differ. Your goal is to calculate from the label’s basis, not to reconstruct how the manufacturer rounded the display.',
        'Use this explicitly illustrative example: the panel states one serving as 1 oz, with 28 grams printed alongside, and lists 150 kcal per serving. You eat 42 grams. Using the stated 28-gram serving gives 1.5 servings and 225 kcal. These invented label values demonstrate serving arithmetic. The printed equivalent is the basis for this example, rather than an assertion that every ounce equals exactly 28 grams.',
      ] },
      { id: 'conversion-chain', title: 'Keep a short conversion chain when no equivalent is printed', paragraphs: [
        'For a weight quantity without a printed gram equivalent, multiply food ounces by approximately 28.35 to obtain grams. An explicitly illustrative 2.5-ounce portion therefore weighs about 70.9 grams. If your matching source is per 100 grams, multiply its nutrient values by about 0.709. Keep the original unit, conversion and nutrition basis together in a companion note outside the app.',
        'For liquids, convert volume to volume and use compatible per-volume nutrition. A conversion from millilitres to grams additionally needs suitable density information, so it is not an automatic next step. Avoid chains that repeatedly switch between guessed cups, ounces and servings. If the amount remains uncertain, record a sensible estimate and its original description rather than adding precision through extra conversions.',
      ] },
      { id: 'travel-entry', title: 'Test your travel reference with Nexal before reusing it', paragraphs: [
        'Download Nexal for Android, create your account and try free manual meal and macro logging with the calculated travel portion. Do the unit arithmetic outside the app, then enter the resulting meal nutrition through the supported workflow. Keep the local label and unit explanation in your own companion reference. This guide does not promise automatic regional-unit detection or every possible unit setting.',
        'When copying a recent meal abroad, confirm the product, serving size and unit rather than trusting a familiar brand name. Premium barcode scanning is optional and still needs comparison with the current package. Close the entry after checking that the food amount and nutrition basis use compatible quantities. The useful result is a readable travel record, without pretending a unit conversion removes uncertainty about an unfamiliar recipe.',
      ] },
    ],
    feature: { href: '/calorie-macro-tracker', label: 'Try a clear travel meal record in Nexal', text: 'Use free manual tracking after checking the local label and portion units.' },
    sources: [{ href: 'https://www.nist.gov/pml/owm/metric-si/unit-conversion/approximate-conversions-us-customary-measures-metric', label: 'NIST: approximate ounce and US fluid-ounce metric conversions' }],
    faqs: [
      { question: 'Are ounces and fluid ounces interchangeable?', answer: 'No. Food weight ounces describe mass, while fluid ounces describe volume. Use a matching conversion and the metric equivalent on the label where available.' },
      { question: 'Should I use 28 or 28.35 grams for a labelled ounce serving?', answer: 'For scaling a label, use its stated metric serving equivalent when provided. Approximately 28.35 grams converts an ordinary weight ounce when you need a separate conversion.' },
    ],
  },
];
